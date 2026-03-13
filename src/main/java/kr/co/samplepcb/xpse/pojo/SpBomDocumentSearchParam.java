package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "BOM 문서 검색 파라미터")
public class SpBomDocumentSearchParam extends SearchParam {

    @Schema(description = "파일명")
    private String fileName;

    @Schema(description = "내용 해시")
    private String contentHash;

    @Schema(description = "파일 타입(확장자)")
    private String type;

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
