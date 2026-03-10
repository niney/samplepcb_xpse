package kr.co.samplepcb.xpse.pojo;

import kr.co.samplepcb.xpse.domain.entity.G5Member;
import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import kr.co.samplepcb.xpse.domain.entity.G5ShopItem;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpOrderListDTO {

    private int ctId;
    private long odId;
    private String mbId;
    private String itId;
    private String itName;
    private int ctPrice;
    private int ctPoint;
    private String ctStatus;
    private String ctOption;
    private int ctQty;
    private Date ctTime;

    private String itemMaker;
    private String itemModel;
    private String itemBrand;
    private int itemPrice;
    private String itemImg1;
    private String itemCompanyName;
    private String itemMemberName;
    private String itemMemberTel;
    private String itemMemberMail;
    private String itemEta;
    private String itemEstimateStatus;

    private int memberNo;
    private String memberName;
    private String memberNick;
    private String memberEmail;
    private String memberTel;
    private String memberHp;

    private List<PartnerOrderDTO> partnerOrders;

    public static SpOrderListDTO from(G5ShopCart cart) {
        SpOrderListDTO dto = new SpOrderListDTO();
        dto.setCtId(cart.getCtId());
        dto.setOdId(cart.getOdId());
        dto.setMbId(cart.getMbId());
        dto.setItId(cart.getItId());
        dto.setItName(cart.getItName());
        dto.setCtPrice(cart.getCtPrice());
        dto.setCtPoint(cart.getCtPoint());
        dto.setCtStatus(cart.getCtStatus());
        dto.setCtOption(cart.getCtOption());
        dto.setCtQty(cart.getCtQty());
        dto.setCtTime(cart.getCtTime());

        G5ShopItem item = cart.getShopItem();
        if (item != null) {
            dto.setItemMaker(item.getItMaker());
            dto.setItemModel(item.getItModel());
            dto.setItemBrand(item.getItBrand());
            dto.setItemPrice(item.getItPrice());
            dto.setItemImg1(item.getItImg1());
            dto.setItemCompanyName(item.getItCompanyName());
            dto.setItemMemberName(item.getItMemberName());
            dto.setItemMemberTel(item.getItMemberTel());
            dto.setItemMemberMail(item.getItMemberMail());
            dto.setItemEta(item.getItEta());
            dto.setItemEstimateStatus(item.getIt24());

            List<SpPartnerOrder> poList = item.getPartnerOrders();
            if (poList != null && !poList.isEmpty()) {
                List<PartnerOrderDTO> poDtos = new ArrayList<>();
                for (SpPartnerOrder po : poList) {
                    poDtos.add(PartnerOrderDTO.from(po));
                }
                dto.setPartnerOrders(poDtos);
            }
        }

        G5Member member = cart.getMember();
        if (member != null) {
            dto.setMemberNo(member.getMbNo());
            dto.setMemberName(member.getMbName());
            dto.setMemberNick(member.getMbNick());
            dto.setMemberEmail(member.getMbEmail());
            dto.setMemberTel(member.getMbTel());
            dto.setMemberHp(member.getMbHp());
        }

        return dto;
    }

    public static class PartnerOrderDTO {
        private long id;
        private int partnerMbNo;
        private String status;
        private int isSelectPartner;
        private int price;
        private String forwarder;
        private Date shipping;
        private String tracking;
        private String memo;
        private Date writeDate;
        private Date modifyDate;

        public static PartnerOrderDTO from(SpPartnerOrder po) {
            PartnerOrderDTO dto = new PartnerOrderDTO();
            dto.setId(po.getId());
            dto.setPartnerMbNo(po.getPartnerMbNo());
            dto.setStatus(po.getStatus());
            dto.setIsSelectPartner(po.getIsSelectPartner());
            dto.setPrice(po.getPrice());
            dto.setForwarder(po.getForwarder());
            dto.setShipping(po.getShipping());
            dto.setTracking(po.getTracking());
            dto.setMemo(po.getMemo());
            dto.setWriteDate(po.getWriteDate());
            dto.setModifyDate(po.getModifyDate());
            return dto;
        }

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public int getPartnerMbNo() { return partnerMbNo; }
        public void setPartnerMbNo(int partnerMbNo) { this.partnerMbNo = partnerMbNo; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getIsSelectPartner() { return isSelectPartner; }
        public void setIsSelectPartner(int isSelectPartner) { this.isSelectPartner = isSelectPartner; }
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
        public String getForwarder() { return forwarder; }
        public void setForwarder(String forwarder) { this.forwarder = forwarder; }
        public Date getShipping() { return shipping; }
        public void setShipping(Date shipping) { this.shipping = shipping; }
        public String getTracking() { return tracking; }
        public void setTracking(String tracking) { this.tracking = tracking; }
        public String getMemo() { return memo; }
        public void setMemo(String memo) { this.memo = memo; }
        public Date getWriteDate() { return writeDate; }
        public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }
        public Date getModifyDate() { return modifyDate; }
        public void setModifyDate(Date modifyDate) { this.modifyDate = modifyDate; }
    }

    public int getCtId() { return ctId; }
    public void setCtId(int ctId) { this.ctId = ctId; }
    public long getOdId() { return odId; }
    public void setOdId(long odId) { this.odId = odId; }
    public String getMbId() { return mbId; }
    public void setMbId(String mbId) { this.mbId = mbId; }
    public String getItId() { return itId; }
    public void setItId(String itId) { this.itId = itId; }
    public String getItName() { return itName; }
    public void setItName(String itName) { this.itName = itName; }
    public int getCtPrice() { return ctPrice; }
    public void setCtPrice(int ctPrice) { this.ctPrice = ctPrice; }
    public int getCtPoint() { return ctPoint; }
    public void setCtPoint(int ctPoint) { this.ctPoint = ctPoint; }
    public String getCtStatus() { return ctStatus; }
    public void setCtStatus(String ctStatus) { this.ctStatus = ctStatus; }
    public String getCtOption() { return ctOption; }
    public void setCtOption(String ctOption) { this.ctOption = ctOption; }
    public int getCtQty() { return ctQty; }
    public void setCtQty(int ctQty) { this.ctQty = ctQty; }
    public Date getCtTime() { return ctTime; }
    public void setCtTime(Date ctTime) { this.ctTime = ctTime; }
    public String getItemMaker() { return itemMaker; }
    public void setItemMaker(String itemMaker) { this.itemMaker = itemMaker; }
    public String getItemModel() { return itemModel; }
    public void setItemModel(String itemModel) { this.itemModel = itemModel; }
    public String getItemBrand() { return itemBrand; }
    public void setItemBrand(String itemBrand) { this.itemBrand = itemBrand; }
    public int getItemPrice() { return itemPrice; }
    public void setItemPrice(int itemPrice) { this.itemPrice = itemPrice; }
    public String getItemImg1() { return itemImg1; }
    public void setItemImg1(String itemImg1) { this.itemImg1 = itemImg1; }
    public String getItemCompanyName() { return itemCompanyName; }
    public void setItemCompanyName(String itemCompanyName) { this.itemCompanyName = itemCompanyName; }
    public String getItemMemberName() { return itemMemberName; }
    public void setItemMemberName(String itemMemberName) { this.itemMemberName = itemMemberName; }
    public String getItemMemberTel() { return itemMemberTel; }
    public void setItemMemberTel(String itemMemberTel) { this.itemMemberTel = itemMemberTel; }
    public String getItemMemberMail() { return itemMemberMail; }
    public void setItemMemberMail(String itemMemberMail) { this.itemMemberMail = itemMemberMail; }
    public String getItemEta() { return itemEta; }
    public void setItemEta(String itemEta) { this.itemEta = itemEta; }
    public String getItemEstimateStatus() { return itemEstimateStatus; }
    public void setItemEstimateStatus(String itemEstimateStatus) { this.itemEstimateStatus = itemEstimateStatus; }
    public int getMemberNo() { return memberNo; }
    public void setMemberNo(int memberNo) { this.memberNo = memberNo; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getMemberNick() { return memberNick; }
    public void setMemberNick(String memberNick) { this.memberNick = memberNick; }
    public String getMemberEmail() { return memberEmail; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
    public String getMemberTel() { return memberTel; }
    public void setMemberTel(String memberTel) { this.memberTel = memberTel; }
    public String getMemberHp() { return memberHp; }
    public void setMemberHp(String memberHp) { this.memberHp = memberHp; }
    public List<PartnerOrderDTO> getPartnerOrders() { return partnerOrders; }
    public void setPartnerOrders(List<PartnerOrderDTO> partnerOrders) { this.partnerOrders = partnerOrders; }
}
