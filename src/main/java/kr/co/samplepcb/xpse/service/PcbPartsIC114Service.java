package kr.co.samplepcb.xpse.service;

import coolib.common.CCResult;
import coolib.util.CommonUtils;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PcbPartsIC114Service {

    private static final Logger log = LoggerFactory.getLogger(PcbPartsIC114Service.class);
    private static final int EXCEL_CHUNK_SIZE = 3000;
    private static final int MAX_FILE_SIZE_BYTES = 1024 * 1024 * 1024; // 1GB

    /**
     * 주어진 엑셀 시트의 특정 행 구간에서 IC114 인덱싱을 수행합니다.
     *
     * @param sheet 엑셀 시트
     * @param startRow 처리할 시작 행 번호
     * @param endRow 처리할 끝 행 번호
     * @param category 카테고리 정보
     */
    private void excelIndexingByIC114(XSSFSheet sheet, int startRow, int endRow, String category) {
        // TODO: IC114 인덱싱 로직 구현
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

        for (int i = 0; i < rows; i += EXCEL_CHUNK_SIZE) {
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
}
