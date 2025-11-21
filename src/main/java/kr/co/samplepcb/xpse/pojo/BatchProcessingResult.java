package kr.co.samplepcb.xpse.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 배치 파일 처리 결과를 담는 DTO 클래스입니다.
 */
public class BatchProcessingResult {
    private int totalFiles;
    private int successCount;
    private int failureCount;
    private List<FileProcessingResult> results;

    public BatchProcessingResult() {
        this.totalFiles = 0;
        this.successCount = 0;
        this.failureCount = 0;
        this.results = new ArrayList<>();
    }

    public void addResult(FileProcessingResult result) {
        this.results.add(result);
        this.totalFiles++;
        if (result.isSuccess()) {
            this.successCount++;
        } else {
            this.failureCount++;
        }
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public List<FileProcessingResult> getResults() {
        return results;
    }

    public boolean isAllSuccess() {
        return failureCount == 0 && totalFiles > 0;
    }

    @Override
    public String toString() {
        return "BatchProcessingResult{" +
                "totalFiles=" + totalFiles +
                ", successCount=" + successCount +
                ", failureCount=" + failureCount +
                ", results=" + results +
                '}';
    }
}
