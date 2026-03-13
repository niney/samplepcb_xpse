package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Date;
import java.util.List;

@Schema(description = "협력사 견적 항목 상세")
public class SpPartnerEstimateItemDetailDTO {

    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Schema(description = "ID")
    private long id;
    @Schema(description = "견적 항목 ID")
    private long estimateItemId;
    @Schema(description = "파트너 회원번호")
    private int mbNo;
    @Schema(description = "주문 상태")
    private String status;
    @Schema(description = "메모")
    private String memo;
    @Schema(description = "선택된 가격 (JSON)")
    private Object selectedPrice;
    @Schema(description = "작성일")
    private Date writeDate;
    @Schema(description = "수정일")
    private Date modifyDate;

    @Schema(description = "파트너 회원 ID")
    private String partnerMbId;
    @Schema(description = "파트너 회원명")
    private String partnerName;
    @Schema(description = "파트너 닉네임")
    private String partnerNick;
    @Schema(description = "파트너 이메일")
    private String partnerEmail;
    @Schema(description = "파트너 휴대폰번호")
    private String partnerHp;

    @Schema(description = "첨부 파일 목록")
    private List<FileDTO> files;

    public SpPartnerEstimateItemDetailDTO() {}

    public SpPartnerEstimateItemDetailDTO(long id, long estimateItemId, int mbNo,
                                           String status, String memo, String selectedPrice,
                                           Date writeDate, Date modifyDate,
                                           String partnerMbId, String partnerName, String partnerNick,
                                           String partnerEmail, String partnerHp) {
        this.id = id;
        this.estimateItemId = estimateItemId;
        this.mbNo = mbNo;
        this.status = status;
        this.memo = memo;
        this.selectedPrice = parseJson(selectedPrice);
        this.writeDate = writeDate;
        this.modifyDate = modifyDate;
        this.partnerMbId = partnerMbId;
        this.partnerName = partnerName;
        this.partnerNick = partnerNick;
        this.partnerEmail = partnerEmail;
        this.partnerHp = partnerHp;
    }

    @Schema(description = "첨부 파일")
    public static class FileDTO {
        private Long id;
        private String uploadFileName;
        private String originFileName;
        private String pathToken;
        private long size;
        private Date writeDate;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUploadFileName() { return uploadFileName; }
        public void setUploadFileName(String uploadFileName) { this.uploadFileName = uploadFileName; }
        public String getOriginFileName() { return originFileName; }
        public void setOriginFileName(String originFileName) { this.originFileName = originFileName; }
        public String getPathToken() { return pathToken; }
        public void setPathToken(String pathToken) { this.pathToken = pathToken; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        public Date getWriteDate() { return writeDate; }
        public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
    }

    private static Object parseJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(value, Object.class);
        } catch (JacksonException e) {
            return value;
        }
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getEstimateItemId() { return estimateItemId; }
    public void setEstimateItemId(long estimateItemId) { this.estimateItemId = estimateItemId; }
    public int getMbNo() { return mbNo; }
    public void setMbNo(int mbNo) { this.mbNo = mbNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public Object getSelectedPrice() { return selectedPrice; }
    public void setSelectedPrice(Object selectedPrice) { this.selectedPrice = selectedPrice; }
    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
    public Date getModifyDate() { return modifyDate; }
    public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
    public String getPartnerMbId() { return partnerMbId; }
    public void setPartnerMbId(String partnerMbId) { this.partnerMbId = partnerMbId; }
    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
    public String getPartnerNick() { return partnerNick; }
    public void setPartnerNick(String partnerNick) { this.partnerNick = partnerNick; }
    public String getPartnerEmail() { return partnerEmail; }
    public void setPartnerEmail(String partnerEmail) { this.partnerEmail = partnerEmail; }
    public String getPartnerHp() { return partnerHp; }
    public void setPartnerHp(String partnerHp) { this.partnerHp = partnerHp; }
    public List<FileDTO> getFiles() { return files; }
    public void setFiles(List<FileDTO> files) { this.files = files; }
}
