package kr.co.samplepcb.xpse.pojo;

/**
 * 파일 처리 결과를 담는 DTO 클래스입니다.
 */
public class FileProcessingResult {
    private String fileName;
    private boolean success;
    private String errorMessage;
    private int processedSheets;
    private int totalRows;

    public FileProcessingResult(String fileName) {
        this.fileName = fileName;
        this.success = false;
        this.processedSheets = 0;
        this.totalRows = 0;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getProcessedSheets() {
        return processedSheets;
    }

    public void setProcessedSheets(int processedSheets) {
        this.processedSheets = processedSheets;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    @Override
    public String toString() {
        return "FileProcessingResult{" +
                "fileName='" + fileName + '\'' +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", processedSheets=" + processedSheets +
                ", totalRows=" + totalRows +
                '}';
    }
}
