package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "BOM 문서 저장 요청")
public class SpBomDocumentCreateDTO {

    @Schema(description = "문서 ID(수정 시 사용)")
    private Long id;

    @Schema(description = "원본 파일명")
    private String fileName;

    @Schema(description = "SHA-256 해시")
    private String contentHash;

    @Schema(description = "파일 정보(JSON)")
    private Object fileInfo;

    @Schema(description = "BOM 아이템(JSON)")
    private Object items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }

    public Object getFileInfo() { return fileInfo; }
    public void setFileInfo(Object fileInfo) { this.fileInfo = fileInfo; }

    public Object getItems() { return items; }
    public void setItems(Object items) { this.items = items; }
}
