package kr.co.samplepcb.xpse.service;

import coolib.util.CommonUtils;
import kr.co.samplepcb.xpse.domain.PcbKindSearch;
import kr.co.samplepcb.xpse.domain.PcbPartsSearch;
import kr.co.samplepcb.xpse.domain.PcbUnitSearch;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import kr.co.samplepcb.xpse.repository.PcbKindSearchRepository;
import kr.co.samplepcb.xpse.repository.PcbPartsSearchRepository;
import kr.co.samplepcb.xpse.service.common.sub.ExcelSubService;
import kr.co.samplepcb.xpse.util.CoolStringUtils;
import kr.co.samplepcb.xpse.util.PcbPartsUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PcbPartsService {

    private static final Logger log = LoggerFactory.getLogger(PcbPartsService.class);

    // search
    private final PcbPartsSearchRepository pcbPartsSearchRepository;
    private final PcbKindSearchRepository pcbKindSearchRepository;

    // service
    private final ExcelSubService excelSubService;

    public PcbPartsService(PcbPartsSearchRepository pcbPartsSearchRepository, PcbKindSearchRepository pcbKindSearchRepository, ExcelSubService excelSubService) {
        this.pcbPartsSearchRepository = pcbPartsSearchRepository;
        this.pcbKindSearchRepository = pcbKindSearchRepository;
        this.excelSubService = excelSubService;
    }

    /**
     * 주어진 propertyName과 value를 파싱하여 PcbUnitSearch 객체에 변환하여 반환합니다.
     *
     * @param propertyName 변환할 속성의 이름
     * @param value 변환할 속성의 값
     * @return 변환된 PcbUnitSearch 객체
     */
    private PcbUnitSearch parsingToPcbUnitSearch(String propertyName, String value) {
        PcbUnitSearch pcbUnitSearch = new PcbUnitSearch();
        List<String> parsedSearchResults = PcbPartsUtils.parseString(value).get(propertyName);
        if (CollectionUtils.isNotEmpty(parsedSearchResults)) {
            String searchValue = parsedSearchResults.getFirst();
            String lowerCaseString = searchValue.toLowerCase();
            // μF와 µF를 uF로 대체
            String replacedString = lowerCaseString
                    .replace("μF", "uF")
                    .replace("µF", "uF")
                    .replace("uf", "uF")
                    .replace("μV", "uV")
                    .replace("µV", "uV")
                    .replace("uv", "uV")
                    .replace("μA", "uA")
                    .replace("µA", "uA")
                    .replace("ua", "uA")
                    .replace("Ω", "Ohm");
            if (propertyName.equals(PcbPartsSearchField.CONDENSER)) {
                // 소문자로 변환
                PcbPartsUtils.FaradsConvert faradsConvert = new PcbPartsUtils.FaradsConvert();
                Map<PcbPartsUtils.FaradsConvert.Unit, String> faradsMap = faradsConvert.convert(replacedString);
                pcbUnitSearch.setField1(faradsMap.get(PcbPartsUtils.PcbConvert.Unit.FARADS));
                pcbUnitSearch.setField2(faradsMap.get(PcbPartsUtils.PcbConvert.Unit.MICROFARADS));
                pcbUnitSearch.setField3(faradsMap.get(PcbPartsUtils.PcbConvert.Unit.NANOFARADS));
                pcbUnitSearch.setField4(faradsMap.get(PcbPartsUtils.PcbConvert.Unit.PICOFARADS));
            }

            if (propertyName.equals(PcbPartsSearchField.TOLERANCE)) {
                PcbPartsUtils.ToleranceConvert toleranceConvert = new PcbPartsUtils.ToleranceConvert();
                Map<PcbPartsUtils.PcbConvert.Unit, String> tolerance = toleranceConvert.convert(replacedString);
                pcbUnitSearch.setField1(tolerance.get(PcbPartsUtils.PcbConvert.Unit.PERCENT));
                pcbUnitSearch.setField2(tolerance.get(PcbPartsUtils.PcbConvert.Unit.PERCENT_STRING));
            }

            if (propertyName.equals(PcbPartsSearchField.OHM)) {
                PcbPartsUtils.OhmConvert ohmConvert = new PcbPartsUtils.OhmConvert();
                Map<PcbPartsUtils.PcbConvert.Unit, String> tolerance = ohmConvert.convert(replacedString);
                pcbUnitSearch.setField1(tolerance.get(PcbPartsUtils.PcbConvert.Unit.OHMS));
                pcbUnitSearch.setField2(tolerance.get(PcbPartsUtils.PcbConvert.Unit.KILOHMS));
                pcbUnitSearch.setField3(tolerance.get(PcbPartsUtils.PcbConvert.Unit.MEGAOHMS));
            }

            if (propertyName.equals(PcbPartsSearchField.VOLTAGE)) {
                PcbPartsUtils.VoltConvert voltageConvert = new PcbPartsUtils.VoltConvert();
                Map<PcbPartsUtils.PcbConvert.Unit, String> voltage = voltageConvert.convert(replacedString);
                pcbUnitSearch.setField1(voltage.get(PcbPartsUtils.PcbConvert.Unit.VOLTS));
                pcbUnitSearch.setField2(voltage.get(PcbPartsUtils.PcbConvert.Unit.KILOVOLTS));
                pcbUnitSearch.setField3(voltage.get(PcbPartsUtils.PcbConvert.Unit.MILLIVOLTS));
                pcbUnitSearch.setField4(voltage.get(PcbPartsUtils.PcbConvert.Unit.MICROVOLTS));
            }

            if (propertyName.equals(PcbPartsSearchField.CURRENT)) {
                PcbPartsUtils.CurrentConvert currentConvert = new PcbPartsUtils.CurrentConvert();
                Map<PcbPartsUtils.PcbConvert.Unit, String> current = currentConvert.convert(replacedString);
                pcbUnitSearch.setField1(current.get(PcbPartsUtils.PcbConvert.Unit.AMPERES));
                pcbUnitSearch.setField2(current.get(PcbPartsUtils.PcbConvert.Unit.MILLIAMPERES));
                pcbUnitSearch.setField3(current.get(PcbPartsUtils.PcbConvert.Unit.MICROAMPERES));
            }

            if (propertyName.equals(PcbPartsSearchField.INDUCTOR)) {
                PcbPartsUtils.InductorConvert inductorConvert = new PcbPartsUtils.InductorConvert();
                Map<PcbPartsUtils.PcbConvert.Unit, String> inductor = inductorConvert.convert(replacedString);
                pcbUnitSearch.setField1(inductor.get(PcbPartsUtils.PcbConvert.Unit.HENRYS));
                pcbUnitSearch.setField2(inductor.get(PcbPartsUtils.PcbConvert.Unit.MILLIHENRYS));
                pcbUnitSearch.setField3(inductor.get(PcbPartsUtils.PcbConvert.Unit.MICROHENRYS));
            }
        }
        return pcbUnitSearch;
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
            String packaging = checkPcbKindExistForCategory(targetPcbKindSearchMap, row, 8, PcbPartsSearchField.PACKAGING);
//            String offerName = checkPcbKindExistForCategory(targetPcbKindSearchMap, row, 17, PcbPartsSearchField.OFFER_NAME);

//            pcbPartsSearch.setLargeCategory(largeCategory);
//            pcbPartsSearch.setMediumCategory(mediumCategory);
//            pcbPartsSearch.setSmallCategory(smallCategory);
            pcbPartsSearch.setPartName(this.excelSubService.getCellStrValue(row, 11));
            pcbPartsSearch.setDescription(this.excelSubService.getCellStrValue(row, 1));
            pcbPartsSearch.setManufacturerName(manufacturerName);
//            pcbPartsSearch.setPartsPackaging(this.excelSubService.getCellStrValue(row, 7));
            pcbPartsSearch.setPackaging(packaging);
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
            pcbPartsSearch.setTolerance(this.excelSubService.getCellStrValue(row, 3));
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

}
