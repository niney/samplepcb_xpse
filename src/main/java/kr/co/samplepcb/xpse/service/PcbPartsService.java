package kr.co.samplepcb.xpse.service;

import tools.jackson.databind.ObjectMapper;
import coolib.common.CCObjectResult;
import coolib.common.CCPagingResult;
import coolib.common.CCResult;
import coolib.common.QueryParam;
import coolib.util.CommonUtils;
import kr.co.samplepcb.xpse.config.ApplicationProperties;
import kr.co.samplepcb.xpse.domain.document.NonDigikeyPartsSearch;
import kr.co.samplepcb.xpse.domain.document.PcbKindSearch;
import kr.co.samplepcb.xpse.domain.document.PcbPartsSearch;
import kr.co.samplepcb.xpse.domain.document.PcbUnitSearch;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import kr.co.samplepcb.xpse.pojo.PcbPkgType;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchVM;
import kr.co.samplepcb.xpse.pojo.adapter.PagingAdapter;
import kr.co.samplepcb.xpse.repository.NonDigikeyPartsSearchRepository;
import kr.co.samplepcb.xpse.repository.PcbKindSearchRepository;
import kr.co.samplepcb.xpse.repository.PcbPartsRepository;
import kr.co.samplepcb.xpse.repository.PcbPartsSearchRepository;
import kr.co.samplepcb.xpse.service.common.sub.DataExtractorSubService;
import kr.co.samplepcb.xpse.service.common.sub.DigikeyPartsParserSubService;
import kr.co.samplepcb.xpse.service.common.sub.DigikeySubService;
import kr.co.samplepcb.xpse.service.common.sub.ExcelSubService;
import kr.co.samplepcb.xpse.service.common.sub.PcbPartsConvertSubService;
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
import org.springframework.core.io.ClassPathResource;
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
    private static final int EXCEL_CHUNK_SIZE = 3000;
    private static final int MAX_FILE_SIZE_BYTES = 1024 * 1024 * 1024; // 1GB

    // manufacturers.json Name -> Id 매핑
    private static final Map<String, Integer> MANUFACTURER_ID_MAP = new HashMap<>();
    static {
        try {
            ClassPathResource resource = new ClassPathResource("manufacturers.json");
            String json = new String(resource.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(json, Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> manufacturers = (List<Map<String, Object>>) data.get("Manufacturers");
            if (manufacturers != null) {
                for (Map<String, Object> m : manufacturers) {
                    String name = (String) m.get("Name");
                    Number id = (Number) m.get("Id");
                    if (name != null && id != null) {
                        MANUFACTURER_ID_MAP.put(name, id.intValue());
                    }
                }
            }
            log.info("manufacturers.json 로드 완료: {}건", MANUFACTURER_ID_MAP.size());
        } catch (IOException e) {
            log.error("manufacturers.json 로드 실패", e);
        }
    }

    private final ApplicationProperties applicationProperties;

    // search
    private final ElasticsearchOperations elasticsearchOperations;
    private final PcbPartsSearchRepository pcbPartsSearchRepository;
    private final PcbKindSearchRepository pcbKindSearchRepository;
    private final NonDigikeyPartsSearchRepository nonDigikeyPartsSearchRepository;
    private final PcbPartsRepository pcbPartsRepository;

    // service
    private final ExcelSubService excelSubService;
    private final DataExtractorSubService dataExtractorSubService;
    private final DigikeyPartsParserSubService digikeyPartsParserSubService;
    private final DigikeySubService digikeySubService;
    private final PcbPartsConvertSubService pcbPartsConvertSubService;

    public PcbPartsService(ApplicationProperties applicationProperties, ElasticsearchOperations elasticsearchOperations, PcbPartsSearchRepository pcbPartsSearchRepository, PcbKindSearchRepository pcbKindSearchRepository, NonDigikeyPartsSearchRepository nonDigikeyPartsSearchRepository, PcbPartsRepository pcbPartsRepository, ExcelSubService excelSubService, DataExtractorSubService dataExtractorSubService, DigikeyPartsParserSubService digikeyPartsParserSubService, DigikeySubService digikeySubService, PcbPartsConvertSubService pcbPartsConvertSubService) {
        this.applicationProperties = applicationProperties;
        this.elasticsearchOperations = elasticsearchOperations;
        this.pcbPartsSearchRepository = pcbPartsSearchRepository;
        this.pcbKindSearchRepository = pcbKindSearchRepository;
        this.nonDigikeyPartsSearchRepository = nonDigikeyPartsSearchRepository;
        this.pcbPartsRepository = pcbPartsRepository;
        this.excelSubService = excelSubService;
        this.dataExtractorSubService = dataExtractorSubService;
        this.digikeyPartsParserSubService = digikeyPartsParserSubService;
        this.digikeySubService = digikeySubService;
        this.pcbPartsConvertSubService = pcbPartsConvertSubService;
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
        if (!value.isEmpty()) {
            // 부모값 체크
            String pTargetName = "";
            if (targetName.equals(PcbPartsSearchField.MEDIUM_CATEGORY)) {
                pTargetName = PcbPartsSearchField.LARGE_CATEGORY;
            }
            if (targetName.equals(PcbPartsSearchField.SMALL_CATEGORY)) {
                pTargetName = PcbPartsSearchField.MEDIUM_CATEGORY;
            }
            if (!pTargetName.isEmpty()) { // 현재 검색된 kind 의 부모값과 엑셀의 부모값 일치 하는지 검사
                String parentValue = this.excelSubService.getCellStrValue(row, rowIdx - 1);
                if (StringUtils.isBlank(parentValue)) {
                    return "";
                }
                Optional<PcbKindSearch> parentPcbKindOpt = pcbKindSearchRepository.findById(pcbKindSearch.getpId());
                if (parentPcbKindOpt.isEmpty()) {
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
            String largeCategory;
            String mediumCategory;
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
            int priceValue = CoolStringUtils.extractAndRoundNumber(this.excelSubService.getCellStrValue(row, 13));
            if (priceValue > 0) {
                pcbPartsSearch.setPrices(PcbPartsUtils.createDefaultPrices(priceValue, PcbPkgType.ELEPARTS));
            }
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

        targetPcbKindSearchMap
                .forEach((integer, stringPcbKindSearchMap) -> stringPcbKindSearchMap
                        .forEach((s, pcbKindSearch) -> pcbKindSearchList.add(pcbKindSearch)));

        if (!pcbKindSearchList.isEmpty()) {
            this.pcbKindSearchRepository.saveAll(pcbKindSearchList);
            log.info("pcb parts items, new kind items indexing");
        }

        this.pcbPartsRepository.saveAll(this.pcbPartsConvertSubService.toEntities(pcbPartsSearchList));
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

        for (int i = 0; i < rows; i += EXCEL_CHUNK_SIZE) {
            excelIndexingByEleparts(sheet, i, Math.min(i + EXCEL_CHUNK_SIZE, rows), category);
        }
    }

    /**
     * 주어진 엑셀 파일을 열어 각 시트를 특정 카테고리로 인덱싱합니다.
     *
     * @param file 인덱싱할 엑셀 파일
     * @return 인덱싱 결과
     */
    public CCResult indexAllByEleparts(MultipartFile file) {
        return processExcelFile(file, this::excelIndexingByEleparts);
    }

    /**
     * 엑셀 파일 처리를 위한 공통 메서드입니다.
     *
     * @param file 처리할 엑셀 파일
     * @param indexingFunction 시트별 인덱싱 로직을 수행하는 함수
     * @return 처리 결과
     */
    private CCResult processExcelFile(MultipartFile file, ExcelIndexingFunction indexingFunction) {
        String fileName = file.getOriginalFilename();
        String category = extractCategoryFromFileName(fileName);

        try {
            IOUtils.setByteArrayMaxOverride(MAX_FILE_SIZE_BYTES);
            try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
                int totalSheets = workbook.getNumberOfSheets();
                for (int i = 0; i < totalSheets; i++) {
                    indexingFunction.index(workbook, i, category);
                }
                log.info("Successfully processed {} sheets from file: {}", totalSheets, fileName);
                return CCResult.ok();
            }
        } catch (IOException e) {
            log.error("Failed to read excel file: {}", fileName, e);
            return CCResult.exceptionSimpleMsg(e);
        } catch (Exception e) {
            log.error("Unexpected error during excel indexing: {}", CommonUtils.getFullStackTrace(e));
            return CCResult.exceptionSimpleMsg(e);
        }
    }

    /**
     * 파일명에서 카테고리를 추출합니다.
     *
     * @param fileName 파일명
     * @return 추출된 카테고리 (없으면 빈 문자열)
     */
    private String extractCategoryFromFileName(String fileName) {
        if (fileName != null) {
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "";
    }

    /**
     * 엑셀 시트 인덱싱을 위한 함수형 인터페이스입니다.
     */
    @FunctionalInterface
    private interface ExcelIndexingFunction {
        void index(XSSFWorkbook workbook, int sheetAt, String category);
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
    @SuppressWarnings("unchecked")
    public CCResult search(Pageable pageable, QueryParam queryParam, PcbPartsSearchVM pcbPartsSearchVM, String referencePrefix) {
        CCResult parseSearch = CCResult.dataNotFound();
        if (StringUtils.isNotEmpty(queryParam.getQf()) && queryParam.getQf().equals("parsing")) {
            // 1. part name 키워드 검색 (정확 일치)
            Criteria keywordCriteria = new Criteria(PcbPartsSearchField.PART_NAME + ".keyword").is(queryParam.getQ());
            CCResult keywordResult = searchPartNameWithHighlight(keywordCriteria);
            if (!keywordResult.isResult() || (keywordResult instanceof CCObjectResult &&
                    CollectionUtils.isNotEmpty(((CCObjectResult<List<?>>) keywordResult).getData()))) {
                return keywordResult;
            }

            // 2. 파싱 검색
            parseSearch = this.parseSearch(pageable, queryParam, referencePrefix);
            if (parseSearch instanceof CCPagingResult && !((CCPagingResult<?>) parseSearch).getData().isEmpty()) {
                return parseSearch;
            }
            if (!parseSearch.isResult()) {
                return parseSearch;
            }
        }
        // 3. part name 일반 검색 (q가 없으면 전체 조회)
        Criteria criteria = StringUtils.isNotEmpty(queryParam.getQ())
                ? new Criteria(PcbPartsSearchField.PART_NAME).is(queryParam.getQ())
                : new Criteria();
        Query query = new CriteriaQuery(criteria);
        query.setPageable(pageable);
        SearchHits<PcbPartsSearch> searchHits = this.elasticsearchOperations.search(query, PcbPartsSearch.class);
        List<PcbPartsSearch> list = CoolElasticUtils.unwrapSearchHits(searchHits);
        return PagingAdapter.toCCPagingResult(pageable, list, searchHits.getTotalHits());
    }

    /**
     * Part Name 완전일치 검색을 수행합니다.
     *
     * @param partName         검색할 part name
     * @param manufacturerName 제조사명 필터 (선택)
     * @return 검색 결과를 포함하는 CCResult 객체
     */
    @SuppressWarnings("unchecked")
    public Mono<CCResult> searchExactMatch(String partName, String manufacturerName) {
        if (StringUtils.isEmpty(partName)) {
            return Mono.just(CCResult.dataNotFound());
        }
        // part name 키워드 검색 (정확 일치)
        Criteria keywordCriteria = new Criteria(PcbPartsSearchField.PART_NAME + ".keyword").is(partName);
        if (StringUtils.isNotEmpty(manufacturerName)) {
            keywordCriteria = keywordCriteria.and(new Criteria(PcbPartsSearchField.MANUFACTURER_NAME + ".keyword").is(manufacturerName));
        }
        // ES에서 먼저 검색
        CCResult result = searchPartNameWithHighlight(keywordCriteria);
        if (result.isResult() && CollectionUtils.isNotEmpty(((CCObjectResult<List<?>>) result).getData())) {
            return Mono.just(result);
        }
        // 없는 경우 Digikey에서 조회 후 인덱싱하고 재검색
        Integer manufacturerId = StringUtils.isNotEmpty(manufacturerName) ? MANUFACTURER_ID_MAP.get(manufacturerName) : null;
        Criteria finalKeywordCriteria = keywordCriteria;
        return this.digikeySubService.getProductDetails(partName, manufacturerId)
                .flatMap(resultMap -> {
                    this.indexingByDigikey(partName, resultMap);
                    return Mono.just(searchPartNameWithHighlight(finalKeywordCriteria));
                });
    }

    /**
     * ID로 정확 일치 검색을 수행합니다.
     *
     * @param id 검색할 ID
     * @return 검색 결과를 포함하는 CCResult 객체
     */
    public CCResult searchById(String id) {
        if (StringUtils.isEmpty(id)) {
            return CCResult.dataNotFound();
        }
        Optional<PcbPartsSearch> result = this.pcbPartsSearchRepository.findById(id);
        if (result.isPresent()) {
            return CCObjectResult.setSimpleData(result.get());
        }
        return CCResult.dataNotFound();
    }

    /**
     * Part Name으로 하이라이트 검색을 수행합니다.
     *
     * @param criteria 검색 조건
     * @return 검색 결과를 포함하는 CCResult 객체
     */
    private CCResult searchPartNameWithHighlight(Criteria criteria) {
        HighlightQuery highlightQuery = CoolElasticUtils.createHighlightQuery(Set.of(PcbPartsSearchField.PART_NAME));

        Query query = new CriteriaQuery(criteria);
        query.setHighlightQuery(highlightQuery);

        SearchHits<PcbPartsSearch> searchHits = this.elasticsearchOperations.search(query, PcbPartsSearch.class);
        return CCObjectResult.setSimpleData(CoolElasticUtils.getSourceWithHighlight(searchHits));
    }

    /**
     * 검색 쿼리를 파싱하고 실행한 후 CCResult를 반환합니다.
     *
     * @param pageable        페이징 정보를 담고 있는 Pageable 객체
     * @param queryParam      검색 요청에 대한 QueryParam 객체
     * @param referencePrefix 레퍼런스 prefix
     * @return 쿼리 결과를 포함하는 CCResult 객체
     */
    public CCResult parseSearch(Pageable pageable, QueryParam queryParam, String referencePrefix) {
        String q = queryParam.getQ();
        if (StringUtils.isEmpty(q)) {
            return CCResult.dataNotFound();
        }

        // referencePrefix null-safe 처리
        String safeReferencePrefix = StringUtils.defaultString(referencePrefix);

        // 검색어 파싱
        Map<String, List<String>> parsedKeywords = PcbPartsUtils.parseString(q, safeReferencePrefix);

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
        if ("L".equals(safeReferencePrefix) && (!addedSpecFields.contains(PcbPartsSearchField.INDUCTOR) || !hasSizeConditions)) {
            return CCResult.exceptionSimpleMsg(new Exception("인덕터 검색은 인덕터값과 사이즈가 모두 필요합니다."));
        }
        // 저항 (저항값+ 사이즈) 필수
        if ("R".equals(safeReferencePrefix) && (!addedSpecFields.contains(PcbPartsSearchField.OHM) || !hasSizeConditions)) {
            return CCResult.exceptionSimpleMsg(new Exception("저항 검색은 저항값과 사이즈가 모두 필요합니다."));
        }
        // 캐패시터 (저항값+ 사이즈) 필수
        if ("C".equals(safeReferencePrefix) && !addedSpecFields.contains(PcbPartsSearchField.CONDENSER)) {
            return CCResult.exceptionSimpleMsg(new Exception("캐패시터 검색은 캐패시터값이 필요합니다."));
        }
        // 저항 오차범위 없으면 기본값
        if ("R".equals(safeReferencePrefix) && !addedSpecFields.contains(PcbPartsSearchField.TOLERANCE)) {
            // 오차범위가 없다면 기본 값을 넣어줘야 한다
            addSpecCriteria(PcbPartsUtils.parseString("10%"), criteria, highlightFields);
        }
        // 캐패시터 전압 없으면 기본값
        if ("C".equals(safeReferencePrefix) && !addedSpecFields.contains(PcbPartsSearchField.VOLTAGE)) {
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
                if ("C".equals(safeReferencePrefix)) {
                    // parsedKeywords의 condenser키가 있으면 값에서 "F"을 제거
                    List<String> condenserValues = parsedKeywords.get(PcbPartsSearchField.CONDENSER);
                    if (condenserValues != null) {
                        parsedKeywords.put(PcbPartsSearchField.CONDENSER, condenserValues.stream()
                                .map(value -> value.replace("F", "").trim())
                                .collect(Collectors.toList()));
                    }
                }
                if ("R".equals(safeReferencePrefix)) {
                    // parsedKeywords의 ohm키가 있으면 값에서 "ohm"을 제거
                    List<String> ohmValues = parsedKeywords.get(PcbPartsSearchField.OHM);
                    if (ohmValues != null) {
                        parsedKeywords.put(PcbPartsSearchField.OHM, ohmValues.stream()
                                .map(value -> value.replace("ohm", "").trim())
                                .collect(Collectors.toList()));
                    }
                }
                // 디지키 키워드 검색 수행
                CCResult digikeyResult = searchDigikeyByKeyword(safeReferencePrefix, parsedKeywords);
                if (!digikeyResult.isResult()) {
                    return searchDigikeyByKeyword(safeReferencePrefix, parsedKeywordsCopy);
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
        return searchDigikeyByKeyword(safeReferencePrefix, parsedKeywords);
    }

    @SuppressWarnings("unchecked")
    private CCResult searchDigikeyByKeyword(String referencePrefix, Map<String, List<String>> parsedKeywords) {
        String safeReferencePrefix = StringUtils.defaultString(referencePrefix);
        String parsedKeywordsStr = parsedKeywords.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.joining(" "));

        Mono<CCObjectResult<Map<String, Object>>> resultMono = this.digikeySubService.searchByKeyword(safeReferencePrefix, parsedKeywordsStr, 2, 0);
        CCObjectResult<Map<String, Object>> response = resultMono.block();
        if (response != null) {
            CCResult result = this.digikeyPartsParserSubService.parseProductsFirst(response.getData());
            if (!(result instanceof CCObjectResult<?>)) {
                return CCResult.dataNotFound();
            }
            CCObjectResult<PcbPartsSearch> resultObj = (CCObjectResult<PcbPartsSearch>) result;
            PcbPartsSearch pcbPartsSearch = resultObj.getData();
            indexDigikeySearchResult(pcbPartsSearch);
            return PagingAdapter.toCCPagingResult(parsedKeywordsStr, Pageable.ofSize(1), Collections.singletonList(pcbPartsSearch), 1);
        }
        return CCResult.dataNotFound();
    }

    private void indexDigikeySearchResult(PcbPartsSearch pcbPartsSearch) {
        if (pcbPartsSearch == null || StringUtils.isEmpty(pcbPartsSearch.getPartName())) {
            return;
        }
        PcbPartsSearch existing = this.pcbPartsSearchRepository.findByPartNameKeyword(pcbPartsSearch.getPartName());
        if (existing != null) {
            pcbPartsSearch.setId(existing.getId());
            this.pcbPartsRepository.findByDocId(existing.getId())
                    .ifPresent(existingEntity -> {
                        kr.co.samplepcb.xpse.domain.entity.PcbParts updated = this.pcbPartsConvertSubService.toEntity(pcbPartsSearch);
                        updated.setId(existingEntity.getId());
                        updated.setDocId(existingEntity.getDocId());
                        this.pcbPartsRepository.save(updated);
                    });
            this.pcbPartsSearchRepository.save(pcbPartsSearch);
        } else {
            kr.co.samplepcb.xpse.domain.entity.PcbParts newEntity = this.pcbPartsConvertSubService.toEntity(pcbPartsSearch);
            this.pcbPartsRepository.save(newEntity);
            pcbPartsSearch.setId(newEntity.getDocId());
            this.pcbPartsSearchRepository.save(pcbPartsSearch);
        }
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
     * 주어진 키워드 맵에서 PRODUCT_NAME과 SIZE를 제거한 후, 남은 키워드의 개수를 반환합니다.
     *
     * @param parsedKeywords 키워드가 포함된 맵으로, 키는 문자열이고 값은 문자열 리스트입니다.
     * @return PRODUCT_NAME과 SIZE 키를 제거한 후 남은 키워드 맵의 크기
     */
    private static int checkSizeWithoutProductNameAndSize(Map<String, List<String>> parsedKeywords) {
        HashMap<String, List<String>> copyParsedKeywords = new HashMap<>(parsedKeywords);
        copyParsedKeywords.remove(PcbPartsSearchField.PRODUCT_NAME);
        copyParsedKeywords.remove(PcbPartsSearchField.SIZE);
        return copyParsedKeywords.size();
    }

    /**
     * 지정된 부품 이름(keyword)을 기준으로 Digi-Key가 아닌 부품을 검색합니다.
     *
     * @param partName 검색할 부품의 이름 또는 키워드
     * @return 검색 결과가 존재하지 않으면 데이터 없음(CCResult.dataNotFound())을 반환하며,
     *         결과가 있을 경우 검색된 부품 정보 리스트(CCObjectResult.setSimpleData)를 반환
     */
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
            this.pcbPartsRepository.findByDocId(findPcbParts.getId())
                    .ifPresent(existingEntity -> {
                        kr.co.samplepcb.xpse.domain.entity.PcbParts updated = this.pcbPartsConvertSubService.toEntity(pcbPartsSearch);
                        updated.setId(existingEntity.getId());
                        updated.setDocId(existingEntity.getDocId());
                        this.pcbPartsRepository.save(updated);
                    });
            PcbPartsSearch savedPartSearch = this.pcbPartsSearchRepository.save(pcbPartsSearch);
            CCResult findResult = CCObjectResult.setSimpleData(savedPartSearch);
            findResult.setMessage("Already exists");
            return findResult;
        }
        kr.co.samplepcb.xpse.domain.entity.PcbParts newEntity = this.pcbPartsConvertSubService.toEntity(pcbPartsSearch);
        this.pcbPartsRepository.save(newEntity);
        pcbPartsSearch.setId(newEntity.getDocId());
        return CCObjectResult.setSimpleData(this.pcbPartsSearchRepository.save(pcbPartsSearch));
    }

    /**
     * Digikey 데이터를 기반으로 후보 제품을 검색합니다.
     *
     * @param partName 검색할 제품의 이름
     * @param referencePrefix 참조 접두사 (예: "R" 또는 "C")
     * @param response Digikey API 호출 결과를 포함한 응답 객체
     * @return 검색된 제품 정보가 포함된 CCResult 객체.
     *         데이터가 없거나 조건에 맞지 않으면 dataNotFound를 반환합니다.
     */
    @SuppressWarnings("unchecked")
    public CCResult searchCandidateByDigikey(String partName, String referencePrefix, CCObjectResult<Map<String, Object>> response) {
        if (response == null || !response.isResult()) {
            return CCResult.dataNotFound();
        }
        List<Map<String, Object>> products = (List<Map<String, Object>>) response.getData().get("Products");
        if (CollectionUtils.isEmpty(products)) {
            return CCResult.dataNotFound();
        }
        /*Number categoryId = this.digikeyPartsParserSubService.getNestedNumber(products.getFirst(), "Category", "CategoryId");
        if (StringUtils.isNotEmpty(referencePrefix)) {
            if (referencePrefix.equals("R") && categoryId.intValue() != 2) {
                // 저항인데 검색은 저항이 아닌 경우
                return CCResult.dataNotFound();
            }
            if (referencePrefix.equals("C") && categoryId.intValue() != 3) {
                // 커패시터인데 검색은 커패시터가 아닌 경우
                return CCResult.dataNotFound();
            }
        }*/
        return this.digikeyPartsParserSubService.parseProductsFirst(response.getData());
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
