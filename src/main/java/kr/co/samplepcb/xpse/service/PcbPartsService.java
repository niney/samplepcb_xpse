package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCPagingResult;
import coolib.common.CCResult;
import coolib.common.QueryParam;
import coolib.util.CommonUtils;
import kr.co.samplepcb.xpse.config.ApplicationProperties;
import kr.co.samplepcb.xpse.domain.NonDigikeyPartsSearch;
import kr.co.samplepcb.xpse.domain.PcbKindSearch;
import kr.co.samplepcb.xpse.domain.PcbPartsSearch;
import kr.co.samplepcb.xpse.domain.PcbUnitSearch;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchVM;
import kr.co.samplepcb.xpse.pojo.adapter.PagingAdapter;
import kr.co.samplepcb.xpse.repository.NonDigikeyPartsSearchRepository;
import kr.co.samplepcb.xpse.repository.PcbKindSearchRepository;
import kr.co.samplepcb.xpse.repository.PcbPartsSearchRepository;
import kr.co.samplepcb.xpse.service.common.sub.DataExtractorSubService;
import kr.co.samplepcb.xpse.service.common.sub.DigikeyPartsParserSubService;
import kr.co.samplepcb.xpse.service.common.sub.DigikeySubService;
import kr.co.samplepcb.xpse.service.common.sub.ExcelSubService;
import kr.co.samplepcb.xpse.util.CoolElasticUtils;
import kr.co.samplepcb.xpse.util.CoolStringUtils;
import kr.co.samplepcb.xpse.util.PcbPartsUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PcbPartsService {

    private static final Logger log = LoggerFactory.getLogger(PcbPartsService.class);

    private final ApplicationProperties applicationProperties;

    // search
    private final ElasticsearchOperations elasticsearchOperations;
    private final PcbPartsSearchRepository pcbPartsSearchRepository;
    private final PcbKindSearchRepository pcbKindSearchRepository;
    private final NonDigikeyPartsSearchRepository nonDigikeyPartsSearchRepository;

    // service
    private final ExcelSubService excelSubService;
    private final DataExtractorSubService dataExtractorSubService;
    private final DigikeyPartsParserSubService digikeyPartsParserSubService;
    private final DigikeySubService digikeySubService;

    public PcbPartsService(ApplicationProperties applicationProperties, ElasticsearchOperations elasticsearchOperations, PcbPartsSearchRepository pcbPartsSearchRepository, PcbKindSearchRepository pcbKindSearchRepository, NonDigikeyPartsSearchRepository nonDigikeyPartsSearchRepository, ExcelSubService excelSubService, DataExtractorSubService dataExtractorSubService, DigikeyPartsParserSubService digikeyPartsParserSubService, DigikeySubService digikeySubService) {
        this.applicationProperties = applicationProperties;
        this.elasticsearchOperations = elasticsearchOperations;
        this.pcbPartsSearchRepository = pcbPartsSearchRepository;
        this.pcbKindSearchRepository = pcbKindSearchRepository;
        this.nonDigikeyPartsSearchRepository = nonDigikeyPartsSearchRepository;
        this.excelSubService = excelSubService;
        this.dataExtractorSubService = dataExtractorSubService;
        this.digikeyPartsParserSubService = digikeyPartsParserSubService;
        this.digikeySubService = digikeySubService;
    }

    /**
     * 주어진 propertyName과 value를 파싱하여 PcbUnitSearch 객체에 변환하여 반환합니다.
     *
     * @param propertyName 변환할 속성의 이름
     * @param value 변환할 속성의 값
     * @return 변환된 PcbUnitSearch 객체
     */
    private PcbUnitSearch parsingToPcbUnitSearch(String propertyName, String value) {
        return PcbPartsUtils.parsingToPcbUnitSearch(propertyName, value);
    }

    /**
     * pcb kind 에 존재 하지는 검사 하여 없으면 넣지 않는다
     *
     * @param row        로우
     * @param rowIdx     인덱스
     * @param targetName 대상명
     * @return 아이템명
     */
    private String checkPcbKindExistForCategory(XSSFRow row, int rowIdx, String targetName) {
        Integer target = PcbPartsSearchField.PCB_PART_TARGET_IDX_COLUMN.get(targetName);
        String value = this.excelSubService.getCellStrValue(row, rowIdx);
        if (StringUtils.isBlank(value)) {
            return "";
        }
        PcbKindSearch pcbKindSearch = pcbKindSearchRepository.findByItemNameKeywordAndTarget(value, target); // 아이템명으로 검색
        if (pcbKindSearch == null) {
            pcbKindSearch = pcbKindSearchRepository.findByDisplayNameKeywordAndTarget(value, target); // 디시플레이명으로 검색
            if (pcbKindSearch == null) {
                value = "";
            } else {
                value = pcbKindSearch.getItemName();
            }
        }
        if (!value.equals("")) {
            // 부모값 체크
            String pTargetName = "";
            if (targetName.equals(PcbPartsSearchField.MEDIUM_CATEGORY)) {
                pTargetName = PcbPartsSearchField.LARGE_CATEGORY;
            }
            if (targetName.equals(PcbPartsSearchField.SMALL_CATEGORY)) {
                pTargetName = PcbPartsSearchField.MEDIUM_CATEGORY;
            }
            if (!pTargetName.equals("")) { // 현재 검색된 kind 의 부모값과 엑셀의 부모값 일치 하는지 검사
                String parentValue = this.excelSubService.getCellStrValue(row, rowIdx - 1);
                if (StringUtils.isBlank(parentValue)) {
                    return "";
                }
                Optional<PcbKindSearch> parentPcbKindOpt = pcbKindSearchRepository.findById(pcbKindSearch.getpId());
                if (!parentPcbKindOpt.isPresent()) {
                    return "";
                }
                PcbKindSearch parentPcbKindSearch = parentPcbKindOpt.get();
                if (!parentValue.trim().equals(parentPcbKindSearch.getItemName()) && !parentValue.trim().equals(parentPcbKindSearch.getDisplayName())) {
                    return "";
                }
            }
        }
        return value;
    }

    /**
     * pcb kind 에 이미 존재 하는지 검사혀여 없으면 new kind list 넣어준다
     *
     * @param targetPcbKindSearchMap 새로운 kind map(ref)
     * @param row                    로우
     * @param rowIdx                 인덱스
     * @param targetName             대상명
     * @return 로우 인덱스에서 가져온 데이터
     */
    private String checkPcbKindExistForCategory(Map<Integer, Map<String, PcbKindSearch>> targetPcbKindSearchMap, XSSFRow row, int rowIdx, String targetName) {
        String value = this.excelSubService.getCellStrValue(row, rowIdx);
        makePcbKindIfNotExist(targetPcbKindSearchMap, targetName, value);
        return value;
    }

    /**
     * pcb kind 에 존재 하지 않으면 만들어 리턴
     *
     * @param refTargetPcbKindSearchMap target 별 value 값이 저장된 map
     * @param targetName                대성명
     * @param value                     값
     */
    private void makePcbKindIfNotExist(Map<Integer, Map<String, PcbKindSearch>> refTargetPcbKindSearchMap, String targetName, String value) {
        Map<String, Integer> columnToTargetMap = PcbPartsSearchField.PCB_PART_TARGET_IDX_COLUMN;
        Integer target = columnToTargetMap.get(targetName);
        PcbKindSearch pcbKindSearch = pcbKindSearchRepository.findByItemNameKeywordAndTarget(value, target);
        if (pcbKindSearch == null) {
            // 검색엔진이 없으면 새로생성
            PcbKindSearch newKindSearch = new PcbKindSearch();
            newKindSearch.setItemName(value);
            newKindSearch.setTarget(target);
            Map<String, PcbKindSearch> pcbKindSearchMap = refTargetPcbKindSearchMap.get(target);
            if (pcbKindSearchMap == null) {
                // target 별 데이터에 없으면 생성
                pcbKindSearchMap = new HashMap<>();
                pcbKindSearchMap.put(value, newKindSearch);
                refTargetPcbKindSearchMap.put(target, pcbKindSearchMap);
            } else {
                if (pcbKindSearchMap.get(value) == null) {
                    // target 별 value 데이터에 없으면 생성
                    if (pcbKindSearchMap.isEmpty()) {
                        pcbKindSearchMap = new HashMap<>();
                    }
                    pcbKindSearchMap.put(value, newKindSearch);
                }
            }
        }
    }

    /**
     * 주어진 엑셀 시트의 특정 행 구간에서 Eleparts 인덱싱을 수행합니다.
     *
     * @param sheet 엑셀 시트
     * @param startRow 처리할 시작 행 번호
     * @param endRow 처리할 끝 행 번호
     * @param category 카테고리 정보
     */
    private void excelIndexingByEleparts(XSSFSheet sheet, int startRow, int endRow, String category) {
        List<PcbPartsSearch> pcbPartsSearchList = new ArrayList<>();
        List<PcbKindSearch> pcbKindSearchList = new ArrayList<>();
        Map<String, PcbPartsSearch> pcbPartsSearchMap = new HashMap<>();
        Map<Integer, Map<String, PcbKindSearch>> targetPcbKindSearchMap = new HashMap<>();

        for (int rowIndex = startRow; rowIndex < endRow; rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex); // 각 행을 읽어온다
            if (row == null) {
                continue;
            }

            // 특수문자 제거하여 저장
            String valueStr = this.excelSubService.getCellStrValue(row, 11).trim().replaceAll("[^a-zA-Z0-9]", "");

            PcbPartsSearch findPcbItem = pcbPartsSearchMap.get(valueStr);
            if (findPcbItem != null) {
                continue;
            }
            PcbPartsSearch pcbPartsSearch = new PcbPartsSearch();
            String largeCategory = "";
            String mediumCategory = "";
            // category 00020002 or 00020004라면 largeCategory 값은 Passive Components
            if (category.equals("00020002") || category.equals("00020004")) {
                largeCategory = PcbPartsSearchField.PASSIVE_COMPONENTS;
                pcbPartsSearch.setLargeCategory(largeCategory);
            }
            // category 00020002 이라면  mediumCategory 값은 Capacitors
            if (category.equals("00020002")) {
                mediumCategory = PcbPartsSearchField.CAPACITORS;
                pcbPartsSearch.setMediumCategory(mediumCategory);
            }
            // category 00020004 이라면  mediumCategory 값은 Resistors
            if (category.equals("00020004")) {
                mediumCategory = PcbPartsSearchField.RESISTORS;
                pcbPartsSearch.setMediumCategory(mediumCategory);
            }

            String manufacturerName = checkPcbKindExistForCategory(targetPcbKindSearchMap, row, 12, PcbPartsSearchField.MANUFACTURER_NAME);
//            String packaging = checkPcbKindExistForCategory(targetPcbKindSearchMap, row, 8, PcbPartsSearchField.PACKAGING);
            String offerName = checkPcbKindExistForCategory(targetPcbKindSearchMap, row, 17, PcbPartsSearchField.OFFER_NAME);

//            pcbPartsSearch.setLargeCategory(largeCategory);
//            pcbPartsSearch.setMediumCategory(mediumCategory);
//            pcbPartsSearch.setSmallCategory(smallCategory);
            pcbPartsSearch.setPartName(this.excelSubService.getCellStrValue(row, 11));
            pcbPartsSearch.setDescription(this.excelSubService.getCellStrValue(row, 1));
            pcbPartsSearch.setManufacturerName(manufacturerName);
//            pcbPartsSearch.setPartsPackaging(this.excelSubService.getCellStrValue(row, 7));
            pcbPartsSearch.setPackaging(this.parsingToPcbUnitSearch(PcbPartsSearchField.PACKAGING, this.excelSubService.getCellStrValue(row, 8)));
//            pcbPartsSearch.setMoq(this.excelSubService.getCellNumberValue(row, 9).intValue());
            pcbPartsSearch.setPrice1(CoolStringUtils.extractAndRoundNumber(this.excelSubService.getCellStrValue(row, 13)));
            pcbPartsSearch.setPrice2(CoolStringUtils.extractAndRoundNumber(this.excelSubService.getCellStrValue(row, 13)));
            pcbPartsSearch.setPrice3(CoolStringUtils.extractAndRoundNumber(this.excelSubService.getCellStrValue(row, 13)));
            pcbPartsSearch.setPrice4(CoolStringUtils.extractAndRoundNumber(this.excelSubService.getCellStrValue(row, 13)));
            pcbPartsSearch.setPrice5(CoolStringUtils.extractAndRoundNumber(this.excelSubService.getCellStrValue(row, 13)));
//            pcbPartsSearch.setInventoryLevel(this.excelSubService.getCellNumberValue(row, 15).intValue());
//            pcbPartsSearch.setMemo(this.excelSubService.getCellStrValue(row, 16));
//            pcbPartsSearch.setOfferName(offerName);
//            pcbPartsSearch.setMemberId(this.excelSubService.getCellStrValue(row, 18));
            pcbPartsSearch.setStatus(PcbPartsSearchField.Status.APPROVED.ordinal());

            // watt
            pcbPartsSearch.setWatt(this.parsingToPcbUnitSearch(PcbPartsSearchField.WATT, this.excelSubService.getCellStrValue(row, 2)));
            // tolerance
            pcbPartsSearch.setTolerance(this.parsingToPcbUnitSearch(PcbPartsSearchField.TOLERANCE, this.excelSubService.getCellStrValue(row, 3)));
            // ohm
            pcbPartsSearch.setOhm(this.parsingToPcbUnitSearch(PcbPartsSearchField.OHM, this.excelSubService.getCellStrValue(row, 4)));
            // condenser
            pcbPartsSearch.setCondenser(this.parsingToPcbUnitSearch(PcbPartsSearchField.CONDENSER, this.excelSubService.getCellStrValue(row, 5)));
            // voltage
            pcbPartsSearch.setVoltage(this.parsingToPcbUnitSearch(PcbPartsSearchField.VOLTAGE, this.excelSubService.getCellStrValue(row, 6)));
            // temperature
            pcbPartsSearch.setTemperature(this.excelSubService.getCellStrValue(row, 7));
            // size
            pcbPartsSearch.setSize(this.excelSubService.getCellStrValue(row, 8));
            // current
            pcbPartsSearch.setCurrent(this.parsingToPcbUnitSearch(PcbPartsSearchField.CURRENT, this.excelSubService.getCellStrValue(row, 9)));
            // inductor
            pcbPartsSearch.setInductor(this.parsingToPcbUnitSearch(PcbPartsSearchField.INDUCTOR, this.excelSubService.getCellStrValue(row, 10)));

            log.info("pcb parts item prepare indexing : parts name={}", valueStr);
            pcbPartsSearchList.add(pcbPartsSearch);
            pcbPartsSearchMap.put(valueStr, pcbPartsSearch);
        }

        targetPcbKindSearchMap.forEach((integer, stringPcbKindSearchMap) -> {
            stringPcbKindSearchMap.forEach((s, pcbKindSearch) -> {
                pcbKindSearchList.add(pcbKindSearch);
            });
        });

        if (!pcbKindSearchList.isEmpty()) {
            this.pcbKindSearchRepository.saveAll(pcbKindSearchList);
            log.info("pcb parts items, new kind items indexing");
        }

        this.pcbPartsSearchRepository.saveAll(pcbPartsSearchList);
        log.info("pcb parts items indexing");
    }

    /**
     * 주어진 엑셀 워크북의 특정 시트를 읽어 지정된 카테고리로 인덱싱합니다.
     *
     * @param workbook 인덱싱할 엑셀 워크북
     * @param sheetAt 인덱싱할 시트의 인덱스
     * @param category 인덱싱할 카테고리
     */
    private void excelIndexingByEleparts(XSSFWorkbook workbook, int sheetAt, String category) {
        XSSFSheet sheet = workbook.getSheetAt(sheetAt);
        int rows = sheet.getPhysicalNumberOfRows();
        if (rows < 1) {
            log.info("pcb parts item indexing, data rows={}", rows);
            return;
        }

        int chunkSize = 3000;
        for (int i = 0; i < rows; i += chunkSize) {
            excelIndexingByEleparts(sheet, i, Math.min(i + chunkSize, rows), category);
        }
    }

    /**
     * 주어진 엑셀 파일을 열어 각 시트를 특정 카테고리로 인덱싱합니다.
     *
     * @param file 인덱싱할 엑셀 파일
     */
    public void indexAllByEleparts(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String category = "";
        if (fileName != null) {
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find()) {
                category = matcher.group(1);
            }
        }

        XSSFWorkbook workbook = null;
        try {
            // 1GB = 1 * 1024 * 1024 * 1024 바이트
            IOUtils.setByteArrayMaxOverride(1 * 1024 * 1024 * 1024);
            workbook = new XSSFWorkbook(file.getInputStream());
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                excelIndexingByEleparts(workbook, i, category);
            }
        } catch (Exception e) {
            log.error(CommonUtils.getFullStackTrace(e));
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * 검색 기능을 수행하는 메서드입니다.
     *
     * @param pageable         페이징 정보를 담고 있는 Pageable 객체
     * @param queryParam       검색 요청에 대한 QueryParam 객체
     * @param pcbPartsSearchVM 검색 조건을 포함하는 PcbPartsSearchVM 객체
     * @param referencePrefix  참조 지정자값
     * @return 검색 결과를 포함하는 CCResult 객체
     */
    public CCResult search(Pageable pageable, QueryParam queryParam, PcbPartsSearchVM pcbPartsSearchVM, String referencePrefix) {
        CCResult parseSearch = CCResult.dataNotFound();
        if (StringUtils.isNotEmpty(queryParam.getQf()) && queryParam.getQf().equals("parsing")) {
            parseSearch = this.parseSearch(pageable, queryParam, referencePrefix);
            if (parseSearch instanceof CCPagingResult && !((CCPagingResult<?>) parseSearch).getData().isEmpty()) {
                return parseSearch;
            }
            if (!parseSearch.isResult()) {
                return parseSearch;
            }
        }
        Criteria criteria = new Criteria(PcbPartsSearchField.PART_NAME).is(queryParam.getQ());
        HighlightQuery highlightQuery = CoolElasticUtils.createHighlightQuery(Set.of(PcbPartsSearchField.PART_NAME));

        Query query = new CriteriaQuery(criteria);
        query.setHighlightQuery(highlightQuery);

        SearchHits<PcbPartsSearch> searchHits = this.elasticsearchOperations.search(query, PcbPartsSearch.class);
        CCResult result = CCObjectResult.setSimpleData(CoolElasticUtils.getSourceWithHighlight(searchHits));
        if (result.isResult()) {
            return result;
        }
        return parseSearch;
    }

    /**
     * 검색 쿼리를 파싱하고 실행한 후 CCResult를 반환합니다.
     *
     * @param pageable        페이징 정보를 담고 있는 Pageable 객체
     * @param queryParam      검색 요청에 대한 QueryParam 객체
     * @param referencePrefix
     * @return 쿼리 결과를 포함하는 CCResult 객체
     */
    public CCResult parseSearch(Pageable pageable, QueryParam queryParam, String referencePrefix) {
        String q = queryParam.getQ();
        if (StringUtils.isEmpty(q)) {
            return CCResult.dataNotFound();
        }

        // 검색어 파싱
        Map<String, List<String>> parsedKeywords = PcbPartsUtils.parseString(q, referencePrefix);

        // 검색 조건 및 하이라이트 필드 설정
        Criteria criteria = new Criteria();
        Set<String> highlightFields = new HashSet<>();
        highlightFields.add(PcbPartsSearchField.PART_NAME);

        // 사양(spec) 관련 검색 조건 추가
        Set<String> addedSpecFields = addSpecCriteria(parsedKeywords, criteria, highlightFields);
        boolean hasSpecConditions = !addedSpecFields.isEmpty();

        // 사이즈 관련 검색 조건 추가
        boolean hasSizeConditions = addSizeCriteria(pageable, queryParam, parsedKeywords, criteria, highlightFields);

        // 인덕터 (인덕터값+사이즈) 필수
        if (referencePrefix.equals("L") && (!addedSpecFields.contains(PcbPartsSearchField.INDUCTOR) || !hasSizeConditions)) {
            return CCResult.exceptionSimpleMsg(new Exception("인덕터 검색은 인덕터값과 사이즈가 모두 필요합니다."));
        }
        // 저항 (저항값+ 사이즈) 필수
        if (referencePrefix.equals("R") && (!addedSpecFields.contains(PcbPartsSearchField.OHM) || !hasSizeConditions)) {
            return CCResult.exceptionSimpleMsg(new Exception("저항 검색은 저항값과 사이즈가 모두 필요합니다."));
        }
        // 캐패시터 (저항값+ 사이즈) 필수
        if (referencePrefix.equals("C") && !addedSpecFields.contains(PcbPartsSearchField.CONDENSER)) {
            return CCResult.exceptionSimpleMsg(new Exception("캐패시터 검색은 캐패시터값이 필요합니다."));
        }
        // 저항 오차범위 없으면 기본값
        if (referencePrefix.equals("R") && !addedSpecFields.contains(PcbPartsSearchField.TOLERANCE)) {
            // 오차범위가 없다면 기본 값을 넣어줘야 한다
            addSpecCriteria(PcbPartsUtils.parseString("10%"), criteria, highlightFields);
        }
        // 캐패시터 전압 없으면 기본값
        if (referencePrefix.equals("C") && !addedSpecFields.contains(PcbPartsSearchField.VOLTAGE)) {
            // 전압이 없다면 기본 값을 넣어줘야 한다
            addSpecCriteria(PcbPartsUtils.parseString("25V"), criteria, highlightFields);
        }

        // 조건이 하나라도 존재하면 검색 실행
        if (hasSpecConditions || hasSizeConditions) {
            // 검색 쿼리 생성
            Query query = new CriteriaQuery(criteria)
                    .setPageable(pageable);
            query.setHighlightQuery(CoolElasticUtils.createHighlightQuery(highlightFields));

            // 검색 실행
            SearchHits<PcbPartsSearch> searchHits = this.elasticsearchOperations.search(query, PcbPartsSearch.class);

            // 검색 결과가 없으면 디지키 검색 수행
            if (!searchHits.hasSearchHits()) {
                Map<String, List<String>> parsedKeywordsCopy = new HashMap<>(parsedKeywords);
                // C, R 단위 처리
                if (referencePrefix.equals("C")) {
                    // parsedKeywords의 condenser키가 있으면 값에서 "F"을 제거
                    List<String> condenserValues = parsedKeywords.get(PcbPartsSearchField.CONDENSER);
                    if (condenserValues != null) {
                        parsedKeywords.put(PcbPartsSearchField.CONDENSER, condenserValues.stream()
                                .map(value -> value.replace("F", "").trim())
                                .collect(Collectors.toList()));
                    }
                }
                if (referencePrefix.equals("R")) {
                    // parsedKeywords의 ohm키가 있으면 값에서 "ohm"을 제거
                    List<String> ohmValues = parsedKeywords.get(PcbPartsSearchField.OHM);
                    if (ohmValues != null) {
                        parsedKeywords.put(PcbPartsSearchField.OHM, ohmValues.stream()
                                .map(value -> value.replace("ohm", "").trim())
                                .collect(Collectors.toList()));
                    }
                }
                // 디지키 키워드 검색 수행
                CCResult digikeyResult = searchDigikeyByKeyword(referencePrefix, parsedKeywords);
                if (!digikeyResult.isResult()) {
                    return searchDigikeyByKeyword(referencePrefix, parsedKeywordsCopy);
                }
                return digikeyResult;
            }

            // 결과가 있으면 페이징 결과 반환
            return PagingAdapter.toCCPagingResult(
                    pageable,
                    CoolElasticUtils.getSourceWithHighlight(searchHits),
                    searchHits.getTotalHits()
            );
        }

        // 조건이 없으면 디지키 키워드 검색 수행
        return searchDigikeyByKeyword(referencePrefix, parsedKeywords);
    }

    @SuppressWarnings("unchecked")
    private CCResult searchDigikeyByKeyword(String referencePrefix, Map<String, List<String>> parsedKeywords) {
        String parsedKeywordsStr = parsedKeywords.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.joining(" "));
        Mono<CCObjectResult<Map<String, Object>>> resultMono = this.digikeySubService.searchByKeyword(referencePrefix, parsedKeywordsStr, 2, 0);
        CCObjectResult<Map<String, Object>> response = resultMono.block();
        if (response != null) {
            CCResult result = this.digikeyPartsParserSubService.parseProductsFirst(response.getData());
            if (!(result instanceof CCObjectResult<?>)) {
                return CCResult.dataNotFound();
            }
            CCObjectResult<PcbPartsSearch> resultObj = (CCObjectResult<PcbPartsSearch>) result;
            return PagingAdapter.toCCPagingResult(parsedKeywordsStr, Pageable.ofSize(1), Collections.singletonList(resultObj.getData()), 1);
        }
        return CCResult.dataNotFound();
    }

    /**
     * 주어진 Pageable, QueryParam, 키워드 맵, Criteria 및 하이라이트 필드를 사용하여
     * 특정 조건을 추가하고, 필요한 값이 없을 경우 적절한 메시지와 함께 CCPagingResult 객체를 반환합니다.
     *
     * @param pageable 페이징 정보를 담고 있는 Pageable 객체
     * @param queryParam 검색 요청에 대한 QueryParam 객체
     * @param parsedKeywords 검색어가 파싱된 후의 키워드 맵
     * @param criteria 검색 조건을 담고 있는 Criteria 객체
     * @param highlightFields 하이라이트할 필드들의 집합
     * @return 특정 조건이 충족되지 않을 경우 메시지와 함께 CCPagingResult 객체를 반환하며,
     *         조건이 충족되는 경우 null을 반환
     */
    private boolean addSizeCriteria(Pageable pageable, QueryParam queryParam, Map<String, List<String>> parsedKeywords, Criteria criteria, Set<String> highlightFields) {
        boolean hasConditionSpec = false;
        String extractedSize = this.dataExtractorSubService.extractSizeFromTitle(queryParam.getQ());
        if (parsedKeywords.get(PcbPartsSearchField.SIZE) == null && StringUtils.isNotEmpty(extractedSize)) {
            criteria.subCriteria(new Criteria().or(PcbPartsSearchField.SIZE_KEYWORD).is(extractedSize));
            highlightFields.add(PcbPartsSearchField.SIZE_KEYWORD);
            hasConditionSpec = true;
        }

        if (parsedKeywords.get(PcbPartsSearchField.OHM) != null && parsedKeywords.size() <= 1) {
            // ohm 이 있다면 Resistor(저항)으로 판단 오차범위는 필수 이다
            if (CollectionUtils.isEmpty(parsedKeywords.get(PcbPartsSearchField.TOLERANCE))) {
                // 오차범위가 없다면
                addOrSubCriteria(PcbPartsSearchField.TOLERANCE_KEYWORD_LIST, "10%", criteria, highlightFields);
                parsedKeywords.put(PcbPartsSearchField.TOLERANCE, Collections.singletonList("10%"));
                hasConditionSpec = true;
            }
        }

        if (parsedKeywords.get(PcbPartsSearchField.CONDENSER) != null && parsedKeywords.size() <= 1) {
            // condenser(콘덴서) 있다면 Capacitor(커패시터)로 판단 하고 전압은 필수 이다
            if (CollectionUtils.isEmpty(parsedKeywords.get(PcbPartsSearchField.VOLTAGE))) {
                // 전압이 없다면
                addOrSubCriteria(PcbPartsSearchField.VOLTAGE_KEYWORD_LIST, "25V", criteria, highlightFields);
                parsedKeywords.put(PcbPartsSearchField.VOLTAGE, Collections.singletonList("25V"));
                hasConditionSpec = true;
            }
        }

//        int parsedWithoutProductNameAndSize = checkSizeWithoutProductNameAndSize(parsedKeywords);
//        if (StringUtils.isNotEmpty(extractedSize)) {
//            parsedWithoutProductNameAndSize += 1;
//        }
//        if (parsedWithoutProductNameAndSize < 2) {
//            CCPagingResult<Object> ccPagingResult = PagingAdapter.toCCPagingResult(pageable, new ArrayList<>(), 0);
//            ccPagingResult.setMessage("2개 이상의 값이 필요합니다.");
//            return ccPagingResult;
//        }
        return hasConditionSpec;
    }


    /**
     * 주어진 필드 이름 목록과 값을 사용하여 Criteria 객체에 조건을 추가합니다.
     *
     * @param parseString     조건을 추가할 필드 이름과 값의 맵
     * @param criteria        기존의 Criteria 객체
     * @param highlightFields 강조 표시할 필드 이름들의 집합
     * @return 추가된 필드명들의 집합
     */
    private static Set<String> addSpecCriteria(Map<String, List<String>> parseString, Criteria criteria, Set<String> highlightFields) {
        Set<String> addedFields = new HashSet<>();
        for (String fieldName : parseString.keySet()) {
            List<String> fieldValue = parseString.get(fieldName);
            String keywords = String.join(" ", fieldValue);
            switch (fieldName) {
                case PcbPartsSearchField.WATT -> {
                    criteria = addOrSubCriteria(PcbPartsSearchField.WATT_KEYWORD_LIST, keywords, criteria, highlightFields);
                    addedFields.add(fieldName);
                }
                case PcbPartsSearchField.TOLERANCE -> {
                    criteria = addOrSubCriteria(PcbPartsSearchField.TOLERANCE_KEYWORD_LIST, keywords, criteria, highlightFields);
                    addedFields.add(fieldName);
                }
                case PcbPartsSearchField.OHM -> {
                    criteria = addOrSubCriteria(PcbPartsSearchField.OHM_KEYWORD_LIST, keywords, criteria, highlightFields);
                    addedFields.add(fieldName);
                }
                case PcbPartsSearchField.CONDENSER -> {
                    criteria = addOrSubCriteria(PcbPartsSearchField.CONDENSER_KEYWORD_LIST, keywords, criteria, highlightFields);
                    addedFields.add(fieldName);
                }
                case PcbPartsSearchField.VOLTAGE -> {
                    criteria = addOrSubCriteria(PcbPartsSearchField.VOLTAGE_KEYWORD_LIST, keywords, criteria, highlightFields);
                    addedFields.add(fieldName);
                }
                case PcbPartsSearchField.CURRENT -> {
                    criteria = addOrSubCriteria(PcbPartsSearchField.CURRENT_KEYWORD_LIST, keywords, criteria, highlightFields);
                    addedFields.add(fieldName);
                }
                case PcbPartsSearchField.INDUCTOR -> {
                    criteria = addOrSubCriteria(PcbPartsSearchField.INDUCTOR_KEYWORD_LIST, keywords, criteria, highlightFields);
                    addedFields.add(fieldName);
                }
            }
        }
        return addedFields;
    }


    /**
     * 주어진 키워드 필드 이름 목록과 키워드를 사용하여 Criteria 객체에 OR 조건을 추가합니다.
     *
     * @param keywordFieldNameList 키워드를 적용할 필드 이름 목록
     * @param keywords 검색할 키워드
     * @param refCriteria 기존의 Criteria 객체
     * @param highlightFields 강조 표시할 필드 이름들의 집합
     * @return OR 조건이 추가된 Criteria 객체
     */
    private static Criteria addOrSubCriteria(List<String> keywordFieldNameList, String keywords, Criteria refCriteria, Set<String> highlightFields) {
        Criteria subCriteria = new Criteria();
        for (String keyword : keywordFieldNameList) {
            subCriteria = subCriteria.or(keyword).is(keywords);
            highlightFields.add(keyword);
        }
        refCriteria = refCriteria.subCriteria(subCriteria);
        return refCriteria;
    }

    /**
     * 'PRODUCT_NAME'과 'SIZE' 키를 제외한 키워드 맵의 크기를 확인합니다.
     *
     * @param tag parsedKeywords 키워드 맵
     * @return 'PRODUCT_NAME'과 'SIZE' 키를 제외한 나머지 키의 개수
     */
    private static int checkSizeWithoutProductNameAndSize(Map<String, List<String>> parsedKeywords) {
        HashMap<String, List<String>> copyParsedKeywords = new HashMap<>(parsedKeywords);
        copyParsedKeywords.remove(PcbPartsSearchField.PRODUCT_NAME);
        copyParsedKeywords.remove(PcbPartsSearchField.SIZE);
        return copyParsedKeywords.size();
    }

    public CCResult searchNonDigikeyParts(String partName) {
        List<NonDigikeyPartsSearch> partsSearches = this.nonDigikeyPartsSearchRepository.findByPartNameKeyword(partName);
        if (partsSearches.isEmpty()) {
            return CCResult.dataNotFound();
        }
        return CCObjectResult.setSimpleData(partsSearches);
    }

    /**
     * Digikey 응답 객체를 기반으로 인덱싱을 수행하는 메서드입니다.
     *
     * @param response Digikey로부터 받은 결과 데이터를 포함하는 CCObjectResult 객체
     * @return 인덱싱 결과를 나타내는 CCResult 객체. 만약 response가 유효하지 않으면 데이터가 없음을 나타내는 CCResult를 반환
     */
    public CCResult indexingByDigikey(String partName, CCObjectResult<Map<String, Object>> response) {
        if (!response.isResult()) {
            CCResult ccResult = CCResult.dataNotFound();
            if (StringUtils.isNotEmpty(response.getMessage())) {
                ccResult.setMessage(response.getMessage());
            }
            NonDigikeyPartsSearch nonDigikeyPartsSearch = new NonDigikeyPartsSearch();
            nonDigikeyPartsSearch.setPartName(partName);
            this.nonDigikeyPartsSearchRepository.save(nonDigikeyPartsSearch);
            return ccResult;
        }
        CCObjectResult<PcbPartsSearch> result = this.digikeyPartsParserSubService.parseProduct(response.getData());
        PcbPartsSearch pcbPartsSearch = result.getData();
        PcbPartsSearch findPcbParts = this.pcbPartsSearchRepository.findByPartNameKeyword(pcbPartsSearch.getPartName());
        if (findPcbParts != null) {
            pcbPartsSearch.setId(findPcbParts.getId());
            PcbPartsSearch savedPartSearch = this.pcbPartsSearchRepository.save(pcbPartsSearch);
            CCResult findResult = CCObjectResult.setSimpleData(savedPartSearch);
            findResult.setMessage("Already exists");
            return findResult;
        }
        return CCObjectResult.setSimpleData(this.pcbPartsSearchRepository.save(pcbPartsSearch));
    }

    /**
     * 주어진 부품 번호에 해당하는 Digi-Key 제품 세부 정보를 검색합니다.
     *
     * @param partNumber 검색할 Digi-Key 제품의 부품 번호
     * @return 검색된 제품 세부 정보를 포함하는 CCObjectResult 객체의 Mono
     */
    public Mono<CCObjectResult<Map<String, Object>>> searchDigikeyProductDetails(String partNumber) {
        return WebClient
                .create(applicationProperties.getMlServer().getServerUrl())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/digikeyProductDetails")
                        .queryParam("partNumber", partNumber)  // 단일 문장 쿼리 파라미터
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CCObjectResult<Map<String, Object>>>() {})
                .doOnError(WebClientResponseException.class, ex ->
                        log.error(ex.getResponseBodyAsString()));
    }
}
