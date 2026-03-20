package kr.co.samplepcb.xpse.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import kr.co.samplepcb.xpse.domain.entity.G5ShopItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Schema(description = "SP 아이템 생성 요청")
public class SpItemCreateDTO {

    @Schema(description = "아이템 ID (미입력 시 System.currentTimeMillis() 자동 생성)")
    private String itId;

    @Schema(description = "아이템명", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itName;

    @Schema(description = "판매가격")
    private int itPrice;

    @Schema(description = "재고수량")
    private int itStockQty;

    @Schema(description = "카테고리 ID")
    private String caId;

    @Schema(description = "상태 (order 또는 rfq, 기본값: order) → it_23 컬럼에 저장")
    private String statusType;

    @Schema(description = "견적상태 → it_24 컬럼에 저장")
    private String status;

    /**
     * PHP itemformupdate_ajax.php ($w == "") 신규등록과 동일한 기본값으로 G5ShopItem 생성.
     * it_use = 1 (활성화), it_time/it_update_time = NOW(), 나머지 string = "", int = 0.
     */
    public G5ShopItem toG5ShopItem(String ipAddress) {
        G5ShopItem item = new G5ShopItem();
        Date now = new Date();

        // 입력값
        item.setItId(itId);
        item.setItName(itName);
        item.setItPrice(itPrice);
        item.setItStockQty(itStockQty);

        // 활성화
        item.setItUse(1);

        // 타임스탬프
        item.setItTime(now);
        item.setItUpdateTime(now);
        item.setItIp(ipAddress);

        // PHP 레거시 기본값: 빈 문자열 (int 필드는 JPA default 0)
        item.setCaId(caId != null ? caId : "");
        item.setCaId2("");
        item.setCaId3("");
        item.setItSkin("");
        item.setItMobileSkin("");
        item.setItMaker("");
        item.setItOrigin("");
        item.setItBrand("");
        item.setItModel("");
        item.setItOptionSubject("");
        item.setItSupplySubject("");
        item.setItBasic("");
        item.setItExplan("");
        item.setItExplan2("");
        item.setItMobileExplan("");
        item.setItSellEmail("");
        item.setItHeadHtml("");
        item.setItTailHtml("");
        item.setItMobileHeadHtml("");
        item.setItMobileTailHtml("");
        item.setItInfoGubun("");
        item.setItInfoValue("");
        item.setItShopMemo("");
        item.setEcMallPid("");
        item.setItCompanyName("");
        item.setItMemberName("");
        item.setItMemberTel("");
        item.setItMemberMail("");
        item.setItMemberMemo("");
        item.setItEta("");

        // NOT NULL 이미지/파일 필드 (DB default '' 이지만 JPA가 NULL 전송 방지)
        item.setItImg1("");
        item.setItImg2("");
        item.setItImg3("");
        item.setItImg4("");
        item.setItImg5("");
        item.setItImg6("");
        item.setItImg7("");
        item.setItImg8("");
        item.setItImg9("");
        item.setItImg10("");
        item.setItFile1("");
        item.setItFile2("");
        item.setItFile3("");
        item.setItFile4("");
        item.setItFile5("");
        item.setItFile6("");
        item.setItFile7("");
        item.setItFile8("");

        // NOT NULL it_1~10 필드
        item.setIt1("");
        item.setIt2("");
        item.setIt3("");
        item.setIt4("");
        item.setIt5("");
        item.setIt6("");
        item.setIt7("");
        item.setIt8("");
        item.setIt9("");
        item.setIt10("");

        // NOT NULL it_30_subj2/3 ~ it_39_subj2/3 필드
        item.setIt30Subj2("");
        item.setIt30Subj3("");
        item.setIt31Subj2("");
        item.setIt31Subj3("");
        item.setIt32Subj2("");
        item.setIt32Subj3("");
        item.setIt33Subj2("");
        item.setIt33Subj3("");
        item.setIt34Subj2("");
        item.setIt34Subj3("");
        item.setIt35Subj2("");
        item.setIt35Subj3("");
        item.setIt36Subj2("");
        item.setIt36Subj3("");
        item.setIt37Subj2("");
        item.setIt37Subj3("");
        item.setIt38Subj2("");
        item.setIt38Subj3("");
        item.setIt39Subj2("");
        item.setIt39Subj3("");

        // NOT NULL decimal 필드
        item.setItUseAvg(BigDecimal.ZERO);

        // status → it_23 (기본값: order), it_23_subj = "status" 고정
        item.setIt23Subj("statusType");
        item.setIt23(statusType != null && !statusType.isBlank() ? statusType : "order");

        // 견적상태 → it_24, it_24_subj = "견적상태" 고정
        item.setIt24Subj("견적상태");
        if (status != null && !status.isBlank()) {
            item.setIt24(status);
        }

        return item;
    }

    /**
     * 기존 G5ShopItem에 대한 업데이트 적용 (PHP $w == 'u' 동작).
     */
    public void applyTo(G5ShopItem item) {
        item.setItName(itName);
        item.setItPrice(itPrice);
        item.setItStockQty(itStockQty);
        if (caId != null) {
            item.setCaId(caId);
        }
        if (statusType != null && !statusType.isBlank()) {
            item.setIt23(statusType);
        }
        if (status != null && !status.isBlank()) {
            item.setIt24(status);
        }
        item.setItUpdateTime(new Date());
    }

    /**
     * PHP cartupdate_ajax.php 장바구니 INSERT와 동일한 기본값으로 G5ShopCart 생성.
     * od_id = yyyyMMddHHmmss + 2자리 (16자리 long), ct_status = "쇼핑", ct_qty = 1.
     */
    public G5ShopCart toG5ShopCart(String mbId, String ipAddress) {
        G5ShopCart cart = new G5ShopCart();
        Date now = new Date();

        // od_id: PHP date('YmdHis') + 2자리 = 16자리 long
        LocalDateTime ldt = LocalDateTime.now();
        String odIdStr = ldt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%02d", ldt.getNano() / 10_000_000);
        cart.setOdId(Long.parseLong(odIdStr));

        // 회원/상품 정보
        cart.setMbId(mbId != null ? mbId : "");
        cart.setItId(itId);
        cart.setItName(itName);

        // 배송비 관련 (item 기본값 = 0)
        cart.setItScType(0);
        cart.setItScMethod(0);
        cart.setItScPrice(0);
        cart.setItScMinimum(0);
        cart.setItScQty(0);

        // 장바구니 상태
        cart.setCtStatus("쇼핑");
        cart.setCtPrice(itPrice);
        cart.setCtPoint(0);
        cart.setCtPointUse(0);
        cart.setCtStockUse(0);
        cart.setCtOption("");
        cart.setCtQty(1);
        cart.setCtNotax(0);

        // 옵션 없음
        cart.setIoId("");
        cart.setIoType(0);
        cart.setIoPrice(0);

        // NOT NULL text 필드
        cart.setCtHistory("");

        // 기타
        cart.setCtTime(now);
        cart.setCtIp(ipAddress);
        cart.setCtSendCost(0);
        cart.setCtDirect(0);
        cart.setCtSelect(0);
        // ct_select_time: NOT NULL, PHP default '0000-00-00 00:00:00' → Java Date(0) = 1970-01-01
        cart.setCtSelectTime(new Date(0));

        return cart;
    }

    /**
     * 기존 G5ShopCart에 대한 업데이트 적용.
     */
    public void applyTo(G5ShopCart cart) {
        cart.setItName(itName);
        cart.setCtPrice(itPrice);
    }

    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public String getItName() { return itName; }
    public void setItName(String itName) { this.itName = itName; }
    public int getItPrice() { return itPrice; }
    public void setItPrice(int itPrice) { this.itPrice = itPrice; }
    public int getItStockQty() { return itStockQty; }
    public void setItStockQty(int itStockQty) { this.itStockQty = itStockQty; }
    public String getCaId() { return caId; }
    public void setCaId(String caId) { this.caId = caId; }
    public String getStatusType() { return statusType; }
    public void setStatusType(String statusType) { this.statusType = statusType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
