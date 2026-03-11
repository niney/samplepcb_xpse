package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrder;

import java.util.Date;

@Schema(description = "협력사 주문 생성 요청")
public class SpPartnerOrderCreateDTO {

    @Schema(description = "아이템 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itId;
    @Schema(description = "파트너 회원번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private int partnerMbNo;
    @Schema(description = "메타 아이템 (JSON)")
    private String metaItem;
    @Schema(description = "주문 상태 (기본값: 협력사 견적요청)")
    private String status;
    @Schema(description = "파트너 선정 여부")
    private int isSelectPartner;
    @Schema(description = "가격")
    private Integer price;
    @Schema(description = "포워더")
    private String forwarder;
    @Schema(description = "배송일")
    private Date shipping;
    @Schema(description = "추적번호")
    private String tracking;
    @Schema(description = "견적 파일 제목")
    private String estimateFile1Subj;
    @Schema(description = "견적 파일")
    private String estimateFile1;
    @Schema(description = "메모")
    private String memo;

    private static final String DEFAULT_STATUS = "협력사 견적요청";

    public SpPartnerOrder toEntity() {
        SpPartnerOrder entity = new SpPartnerOrder();
        entity.setItId(this.itId);
        entity.setPartnerMbNo(this.partnerMbNo);
        applyFields(entity);
        Date now = new Date();
        entity.setWriteDate(now);
        entity.setModifyDate(now);
        return entity;
    }

    public void applyTo(SpPartnerOrder entity) {
        applyFields(entity);
        entity.setModifyDate(new Date());
    }

    private void applyFields(SpPartnerOrder entity) {
        entity.setMetaItem(this.metaItem);
        entity.setStatus(this.status == null || this.status.isBlank() ? DEFAULT_STATUS : this.status);
        entity.setIsSelectPartner(this.isSelectPartner);
        entity.setPrice(this.price);
        entity.setForwarder(this.forwarder);
        entity.setShipping(this.shipping);
        entity.setTracking(this.tracking);
        entity.setEstimateFile1Subj(this.estimateFile1Subj);
        entity.setEstimateFile1(this.estimateFile1);
        entity.setMemo(this.memo);
    }

    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public int getPartnerMbNo() { return partnerMbNo; }
    public void setPartnerMbNo(int partnerMbNo) { this.partnerMbNo = partnerMbNo; }
    public String getMetaItem() { return metaItem; }
    public void setMetaItem(String metaItem) { this.metaItem = metaItem; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getIsSelectPartner() { return isSelectPartner; }
    public void setIsSelectPartner(int isSelectPartner) { this.isSelectPartner = isSelectPartner; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getForwarder() { return forwarder; }
    public void setForwarder(String forwarder) { this.forwarder = forwarder; }
    public Date getShipping() { return shipping; }
    public void setShipping(Date shipping) { this.shipping = shipping; }
    public String getTracking() { return tracking; }
    public void setTracking(String tracking) { this.tracking = tracking; }
    public String getEstimateFile1Subj() { return estimateFile1Subj; }
    public void setEstimateFile1Subj(String estimateFile1Subj) { this.estimateFile1Subj = estimateFile1Subj; }
    public String getEstimateFile1() { return estimateFile1; }
    public void setEstimateFile1(String estimateFile1) { this.estimateFile1 = estimateFile1; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
