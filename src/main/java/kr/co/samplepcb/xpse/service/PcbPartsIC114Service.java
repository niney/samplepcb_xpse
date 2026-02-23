package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import coolib.util.CommonUtils;
import kr.co.samplepcb.xpse.domain.PcbPartsSearch;
import kr.co.samplepcb.xpse.pojo.BatchProcessingResult;
import kr.co.samplepcb.xpse.pojo.FileProcessingResult;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import kr.co.samplepcb.xpse.pojo.PcbPkgType;
import kr.co.samplepcb.xpse.repository.PcbPartsSearchRepository;
import kr.co.samplepcb.xpse.service.common.sub.ExcelSubService;
import kr.co.samplepcb.xpse.util.CoolStringUtils;
import kr.co.samplepcb.xpse.util.PcbPartsUtils;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PcbPartsIC114Service {

    private static final Logger log = LoggerFactory.getLogger(PcbPartsIC114Service.class);
    private static final int EXCEL_CHUNK_SIZE = 3000;
    private static final int MAX_FILE_SIZE_BYTES = 1024 * 1024 * 1024; // 1GB

    private static final Map<String, String> RESISTANCE_CATEGORIES = new HashMap<>();
    private static final Map<String, String> CAPACITOR_CATEGORIES = new HashMap<>();

    static {
        // 일반 저항 (Through-hole resistors)
        RESISTANCE_CATEGORIES.put("030101", "1/4W J 5% 저항");
        RESISTANCE_CATEGORIES.put("030102", "1/2W J 5% 저항");
        RESISTANCE_CATEGORIES.put("030103", "1/4W F 1% 저항");
        RESISTANCE_CATEGORIES.put("030104", "1/2W F 1% 저항");
        RESISTANCE_CATEGORIES.put("030106", "5와트 시멘트저항");
        RESISTANCE_CATEGORIES.put("030107", "10와트 시멘트저항");
        RESISTANCE_CATEGORIES.put("030108", "파워(방열)저항");
        RESISTANCE_CATEGORIES.put("030110", "1/8W J 5% 저항");
        RESISTANCE_CATEGORIES.put("030111", "1/8W J 1% 저항");
        RESISTANCE_CATEGORIES.put("030112", "1W J 5% 메탈필름저항");
        RESISTANCE_CATEGORIES.put("030113", "2W J 5% 메탈필름저항");
        RESISTANCE_CATEGORIES.put("030114", "1/4W B 0.1% 메탈필름저항");
        RESISTANCE_CATEGORIES.put("030116", "3W J 5% 저항");

        // 칩저항 (SMD chip resistors)
        RESISTANCE_CATEGORIES.put("030119", "1005(0402) 칩저항 5%");
        RESISTANCE_CATEGORIES.put("030120", "1005(0402) 칩저항 1%");
        RESISTANCE_CATEGORIES.put("030121", "1608(0603) 칩저항 5%");
        RESISTANCE_CATEGORIES.put("030122", "1608(0603) 칩저항 1%");
        RESISTANCE_CATEGORIES.put("030123", "2012(0805) 칩저항 5%");
        RESISTANCE_CATEGORIES.put("030124", "2012(0805) 칩저항 1%");
        RESISTANCE_CATEGORIES.put("030125", "3216(1206) 칩저항 5%");
        RESISTANCE_CATEGORIES.put("030126", "3216(1206) 칩저항 1%");
        RESISTANCE_CATEGORIES.put("030127", "5025(2010) 칩저항");
        RESISTANCE_CATEGORIES.put("030129", "6432(2512) 칩저항");
        RESISTANCE_CATEGORIES.put("030130", "6432(2512) 칩저항");
        RESISTANCE_CATEGORIES.put("030131", "3225(1210) 칩저항");
        RESISTANCE_CATEGORIES.put("030132", "3225(1210) 칩저항");
        RESISTANCE_CATEGORIES.put("030133", "안티서지 후막 칩 저항기");

        // 어레이저항 (Array resistors)
        RESISTANCE_CATEGORIES.put("030135", "병렬X형A타입 어레이");
        RESISTANCE_CATEGORIES.put("030136", "분리Y형B타입 어레이");
        RESISTANCE_CATEGORIES.put("030137", "복합 어레이");
        RESISTANCE_CATEGORIES.put("030138", "X형A타입 어레이");
        RESISTANCE_CATEGORIES.put("030139", "Y형B타입 어레이");
        RESISTANCE_CATEGORIES.put("030140", "복합형 어레이");
        RESISTANCE_CATEGORIES.put("030142", "Y형B타입 어레이");

        // 특수저항 (Special resistors)
        RESISTANCE_CATEGORIES.put("030144", "션트저항");

        // 콘덴서 (Capacitors)
        CAPACITOR_CATEGORIES.put("030303", "전해 콘덴서 85℃");
        CAPACITOR_CATEGORIES.put("030318", "전해콘덴서 105℃");
        CAPACITOR_CATEGORIES.put("030319", "무극성 전해콘덴서");
        CAPACITOR_CATEGORIES.put("030304", "칩전해 콘덴서");
        CAPACITOR_CATEGORIES.put("030305", "대용량 콘덴서");
        CAPACITOR_CATEGORIES.put("030301", "세라믹 콘덴서");
        CAPACITOR_CATEGORIES.put("030302", "세라믹 고전압");
        CAPACITOR_CATEGORIES.put("030309", "박스콘넥터");
        CAPACITOR_CATEGORIES.put("030310", "적층세라믹(모노)MPB 콘덴서");
        CAPACITOR_CATEGORIES.put("030311", "탄탈 콘덴서");
        CAPACITOR_CATEGORIES.put("030312", "칩탄탈 콘덴서");
        CAPACITOR_CATEGORIES.put("030313", "마이카 콘덴서");
        CAPACITOR_CATEGORIES.put("030306", "폴리에스테르필름");
        CAPACITOR_CATEGORIES.put("030308", "폴리프로필렌필름(PP)");
        CAPACITOR_CATEGORIES.put("030307", "메탈필름(MF)");
        CAPACITOR_CATEGORIES.put("030314", "슈퍼 콘덴서");
        CAPACITOR_CATEGORIES.put("030320", "교류용콘덴서");
        CAPACITOR_CATEGORIES.put("030327", "1005 칩세라믹 콘덴서");
        CAPACITOR_CATEGORIES.put("030328", "1608 칩세라믹 콘덴서");
        CAPACITOR_CATEGORIES.put("030329", "2012 칩세라믹 콘덴서");
        CAPACITOR_CATEGORIES.put("030330", "3216 칩라믹 콘덴서");
        CAPACITOR_CATEGORIES.put("030331", "3225 칩세라믹 콘덴서");
        CAPACITOR_CATEGORIES.put("030332", "1005 칩세라믹 1REEL");
        CAPACITOR_CATEGORIES.put("030333", "1608 칩세라믹 1REEL");
        CAPACITOR_CATEGORIES.put("030334", "2012 칩세라믹 1REEL");
        CAPACITOR_CATEGORIES.put("030316", "오일 콘덴서");
        CAPACITOR_CATEGORIES.put("030315", "가변 콘덴서");
    }

    // service
    private final ExcelSubService excelSubService;
    // repository
    private final PcbPartsSearchRepository pcbPartsSearchRepository;


    public PcbPartsIC114Service(ExcelSubService excelSubService, PcbPartsSearchRepository pcbPartsSearchRepository) {
        this.excelSubService = excelSubService;
        this.pcbPartsSearchRepository = pcbPartsSearchRepository;
    }

    /**
     * 주어진 엑셀 시트의 특정 행 구간에서 IC114 인덱싱을 수행합니다.
     *
     * @param sheet 엑셀 시트
     * @param startRow 처리할 시작 행 번호
     * @param endRow 처리할 끝 행 번호
     * @param category 카테고리 정보
     */
    private void excelIndexingByIC114(XSSFSheet sheet, int startRow, int endRow, String category) {
        List<PcbPartsSearch> pcbPartsSearchList = new ArrayList<>();
        for (int rowIndex = startRow; rowIndex < endRow; rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex); // 각 행을 읽어온다
            if (row == null) {
                continue;
            }
            String categoryCode = this.excelSubService.getCellStrValue(row, 0);
            String name = this.excelSubService.getCellStrValue(row, 1);
            String abbreviation = this.excelSubService.getCellStrValue(row, 2);
            String price = this.excelSubService.getCellStrValue(row, 3);
            String minQuantity = this.excelSubService.getCellStrValue(row, 4);
            String unit = this.excelSubService.getCellStrValue(row, 5);
            String manufacturer = this.excelSubService.getCellStrValue(row, 6);
            String description = this.excelSubService.getCellStrValue(row, 7);
            String inventory = this.excelSubService.getCellStrValue(row, 8);
            String watt = this.excelSubService.getCellStrValue(row, 9);
            String tolerance = this.excelSubService.getCellStrValue(row, 10);
            String ohm = this.excelSubService.getCellStrValue(row, 11);
            String size = this.excelSubService.getCellStrValue(row, 12);
            String condenser = this.excelSubService.getCellStrValue(row, 13);
            String voltage = this.excelSubService.getCellStrValue(row, 14);
            String temperature = this.excelSubService.getCellStrValue(row, 15);

            PcbPartsSearch pcbPartsSearch = new PcbPartsSearch();
            pcbPartsSearch.setServiceType(PcbPkgType.SAMPLEPCB.getValue());
            pcbPartsSearch.setLargeCategory("수동부품");
            String smallCategory = RESISTANCE_CATEGORIES.get(categoryCode);
            pcbPartsSearch.setMediumCategory("저항");
            if (smallCategory == null) {
                pcbPartsSearch.setMediumCategory("콘덴서");
                smallCategory = CAPACITOR_CATEGORIES.get(categoryCode);
            }
            pcbPartsSearch.setSmallCategory(smallCategory);

            if (name != null && !name.isEmpty()) {
                pcbPartsSearch.setPartName(name);
            }
            if (abbreviation != null && !abbreviation.isEmpty()) {
                pcbPartsSearch.setProductName(abbreviation);
            }
            if (description != null && !description.isEmpty()) {
                pcbPartsSearch.setDescription(description);
            }
            if (manufacturer != null && !manufacturer.isEmpty()) {
                pcbPartsSearch.setManufacturerName(manufacturer);
            }
            if (unit != null && !unit.isEmpty()) {
                pcbPartsSearch.setPartsPackaging(unit);
            }
            Integer priceValue = CoolStringUtils.extractNumericValue(price);
            if (priceValue != null && priceValue > 0) {
                pcbPartsSearch.setPrices(PcbPartsUtils.createDefaultPrices(priceValue, PcbPkgType.SAMPLEPCB));
            }
            pcbPartsSearch.setMoq(CoolStringUtils.extractNumericValue(minQuantity));
            if (watt != null && !watt.isEmpty()) {
                pcbPartsSearch.setWatt(PcbPartsUtils.parsingToPcbUnitSearch(PcbPartsSearchField.WATT, watt));
            }
            if (tolerance != null && !tolerance.isEmpty()) {
                pcbPartsSearch.setTolerance(PcbPartsUtils.parsingToPcbUnitSearch(PcbPartsSearchField.TOLERANCE, tolerance));
            }
            if (ohm != null && !ohm.isEmpty()) {
                pcbPartsSearch.setOhm(PcbPartsUtils.parsingToPcbUnitSearch(PcbPartsSearchField.OHM, ohm));
            }
            if (condenser != null && !condenser.isEmpty()) {
                pcbPartsSearch.setCondenser(PcbPartsUtils.parsingToPcbUnitSearch(PcbPartsSearchField.CONDENSER, condenser));
            }
            if (voltage != null && !voltage.isEmpty()) {
                pcbPartsSearch.setVoltage(PcbPartsUtils.parsingToPcbUnitSearch(PcbPartsSearchField.VOLTAGE, voltage));
            }
            if (size != null && !size.isEmpty()) {
                pcbPartsSearch.setSize(size);
            }
            if (temperature != null && !temperature.isEmpty()) {
                pcbPartsSearch.setTemperature(temperature);
            }
            pcbPartsSearchList.add(pcbPartsSearch);
        }

        this.pcbPartsSearchRepository.saveAll(pcbPartsSearchList);
        log.info("Indexed {} rows from sheet {}", pcbPartsSearchList.size(), sheet.getSheetName());
    }

    /**
     * 주어진 엑셀 워크북의 특정 시트를 읽어 지정된 카테고리로 인덱싱합니다.
     *
     * @param workbook 인덱싱할 엑셀 워크북
     * @param sheetAt 인덱싱할 시트의 인덱스
     * @param category 인덱싱할 카테고리
     */
    private void excelIndexingByIC114(XSSFWorkbook workbook, int sheetAt, String category) {
        XSSFSheet sheet = workbook.getSheetAt(sheetAt);
        int rows = sheet.getPhysicalNumberOfRows();
        if (rows < 1) {
            log.info("pcb parts item indexing, data rows={}", rows);
            return;
        }

        for (int i = 1; i < rows; i += EXCEL_CHUNK_SIZE) {
            excelIndexingByIC114(sheet, i, Math.min(i + EXCEL_CHUNK_SIZE, rows), category);
        }
    }

    /**
     * 주어진 엑셀 파일을 열어 IC114 데이터로 인덱싱합니다.
     *
     * @param file 인덱싱할 엑셀 파일
     * @return 인덱싱 결과
     */
    public CCResult indexAllByIC114(MultipartFile file) {
        return processExcelFile(file, this::excelIndexingByIC114);
    }

    /**
     * 여러 엑셀 파일을 열어 IC114 데이터로 일괄 인덱싱합니다.
     *
     * @param files 인덱싱할 엑셀 파일 배열
     * @return 배치 처리 결과
     */
    public CCResult indexAllByIC114Multiple(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return CCResult.requireParam();
        }

        BatchProcessingResult batchResult = new BatchProcessingResult();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                FileProcessingResult fileResult = new FileProcessingResult(file.getOriginalFilename());
                fileResult.setErrorMessage("File is empty");
                batchResult.addResult(fileResult);
                continue;
            }

            FileProcessingResult fileResult = processExcelFileWithDetails(file, this::excelIndexingByIC114);
            batchResult.addResult(fileResult);
        }

        log.info("Batch processing completed. Total: {}, Success: {}, Failed: {}",
                batchResult.getTotalFiles(), batchResult.getSuccessCount(), batchResult.getFailureCount());

        if (batchResult.isAllSuccess()) {
            return CCObjectResult.setSimpleData(batchResult);
        } else if (batchResult.getSuccessCount() > 0) {
            return new CCResult.Builder()
                    .setSuccessMessage("Partial success: " + batchResult.getSuccessCount() + " succeeded, " + batchResult.getFailureCount() + " failed")
                    .build();

        } else {
            return new CCResult.Builder()
                    .setFailMessage("All files failed to process")
                    .build();
        }
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
     * 엑셀 파일 처리를 위한 상세 정보 포함 메서드입니다.
     *
     * @param file 처리할 엑셀 파일
     * @param indexingFunction 시트별 인덱싱 로직을 수행하는 함수
     * @return 파일 처리 결과
     */
    private FileProcessingResult processExcelFileWithDetails(MultipartFile file, ExcelIndexingFunction indexingFunction) {
        String fileName = file.getOriginalFilename();
        FileProcessingResult result = new FileProcessingResult(fileName);
        String category = extractCategoryFromFileName(fileName);

        try {
            IOUtils.setByteArrayMaxOverride(MAX_FILE_SIZE_BYTES);
            try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
                int totalSheets = workbook.getNumberOfSheets();
                int totalRows = 0;

                for (int i = 0; i < totalSheets; i++) {
                    XSSFSheet sheet = workbook.getSheetAt(i);
                    int rows = sheet.getPhysicalNumberOfRows();
                    totalRows += rows;
                    indexingFunction.index(workbook, i, category);
                }

                result.setSuccess(true);
                result.setProcessedSheets(totalSheets);
                result.setTotalRows(totalRows);
                log.info("Successfully processed {} sheets ({} rows) from file: {}", totalSheets, totalRows, fileName);
            }
        } catch (IOException e) {
            result.setErrorMessage("Failed to read file: " + e.getMessage());
            log.error("Failed to read excel file: {}", fileName, e);
        } catch (Exception e) {
            result.setErrorMessage("Processing error: " + e.getMessage());
            log.error("Unexpected error during excel indexing: {}", CommonUtils.getFullStackTrace(e));
        }

        return result;
    }

    /**
     * 파일명에서 카테고리를 추출합니다.
     * 파일명에 포함된 6자리 카테고리 코드를 찾아서 해당하는 카테고리명을 반환합니다.
     *
     * @param fileName 파일명
     * @return 추출된 카테고리명 (매칭되는 코드가 없으면 빈 문자열)
     */
    private String extractCategoryFromFileName(String fileName) {
        if (fileName == null) {
            return "";
        }

        // 6자리 숫자 패턴으로 카테고리 코드 찾기
        Pattern pattern = Pattern.compile("(\\d{6})");
        Matcher matcher = pattern.matcher(fileName);

        while (matcher.find()) {
            String categoryCode = matcher.group(1);
            // 매칭되는 카테고리가 있으면 반환
            String categoryName = RESISTANCE_CATEGORIES.get(categoryCode);
            if (categoryName == null) {
                categoryName = CAPACITOR_CATEGORIES.get(categoryCode);
            }
            if (categoryName != null) {
                log.info("Found category code: {} -> {}", categoryCode, categoryName);
                return categoryName;
            }
        }

        log.warn("No matching category found in filename: {}", fileName);
        return "";
    }

    /**
     * 엑셀 시트 인덱싱을 위한 함수형 인터페이스입니다.
     */
    @FunctionalInterface
    private interface ExcelIndexingFunction {
        void index(XSSFWorkbook workbook, int sheetAt, String category);
    }
}
