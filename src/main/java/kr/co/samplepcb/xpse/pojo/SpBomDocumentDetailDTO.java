package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "BOM 문서 상세 응답")
public class SpBomDocumentDetailDTO {

    @Schema(description = "문서 ID")
    private Long id;

    @Schema(description = "회원 ID")
    private String mbId;

    @Schema(description = "원본 파일명")
    private String fileName;

    @Schema(description = "SHA-256 해시")
    private String contentHash;

    @Schema(description = "파일 정보(JSON)")
    private Object fileInfo;

    @Schema(description = "BOM 아이템(JSON)")
    private Object items;

    @Schema(description = "작성일")
    private Date createdAt;

    @Schema(description = "수정일")
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }

    public Object getFileInfo() { return fileInfo; }
    public void setFileInfo(Object fileInfo) { this.fileInfo = fileInfo; }

    public Object getItems() { return items; }
    public void setItems(Object items) { this.items = items; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
