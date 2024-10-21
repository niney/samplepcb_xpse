package kr.co.samplepcb.xpse.pojo;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PcbPartsSearchVM {

    public final static List<Field> pcbPartsSearchFields = new ArrayList<>();
    static {
        ReflectionUtils.doWithFields(PcbPartsSearchVM.class, field -> {
            if(field.getType() == Integer.class ||
                    field.getType() == String.class ||
                    field.getType() == Date.class ) {
                field.setAccessible(true);
                pcbPartsSearchFields.add(field);
            }
        });
    }

    private String id;
    private List<String> ids;
    private String serviceType;
    private String subServiceType;
    private String largeCategory;
    private String mediumCategory;
    private String smallCategory;
    private String partName;
    private String description;
    private String manufacturerName;
    private String partsPackaging;
    private String packaging;
    private Integer moq;
    private Integer price;
    private Integer price1; // 1~9
    private Integer price2; // 10 ~99
    private Integer price3; // 100~499
    private Integer price4; // 500~999
    private Integer price5; // 1000~
    private Integer inventoryLevel;
    private String memo;
    private String offerName;
    private List<PcbImageVM> images;
    private String dataCode;
    private String memberId;
    private String managerPhoneNumber;
    private String managerName;
    private String managerEmail;
    private Integer status;
    private String watt;
    private String tolerance;
    private String ohm;
    private String condenser;
    private String voltage;
    private String temperature;
    private String size;
    private String current;
    private String inductor;
    private String productName;
    private List<Integer> statusList;
    private String contents;
    private String token;
    private List<PcbPartSpec> specs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getSubServiceType() {
        return subServiceType;
    }

    public void setSubServiceType(String subServiceType) {
        this.subServiceType = subServiceType;
    }

    public String getLargeCategory() {
        return largeCategory;
    }

    public void setLargeCategory(String largeCategory) {
        this.largeCategory = largeCategory;
    }

    public String getMediumCategory() {
        return mediumCategory;
    }

    public void setMediumCategory(String mediumCategory) {
        this.mediumCategory = mediumCategory;
    }

    public String getSmallCategory() {
        return smallCategory;
    }

    public void setSmallCategory(String smallCategory) {
        this.smallCategory = smallCategory;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getPartsPackaging() {
        return partsPackaging;
    }

    public void setPartsPackaging(String partsPackaging) {
        this.partsPackaging = partsPackaging;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public Integer getMoq() {
        return moq;
    }

    public void setMoq(Integer moq) {
        this.moq = moq;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public static List<Field> getPcbPartsSearchFields() {
        return pcbPartsSearchFields;
    }

    public Integer getPrice1() {
        return price1;
    }

    public void setPrice1(Integer price1) {
        this.price1 = price1;
    }

    public Integer getPrice2() {
        return price2;
    }

    public void setPrice2(Integer price2) {
        this.price2 = price2;
    }

    public Integer getPrice3() {
        return price3;
    }

    public void setPrice3(Integer price3) {
        this.price3 = price3;
    }

    public Integer getPrice4() {
        return price4;
    }

    public void setPrice4(Integer price4) {
        this.price4 = price4;
    }

    public Integer getPrice5() {
        return price5;
    }

    public void setPrice5(Integer price5) {
        this.price5 = price5;
    }

    public Integer getInventoryLevel() {
        return inventoryLevel;
    }

    public void setInventoryLevel(Integer inventoryLevel) {
        this.inventoryLevel = inventoryLevel;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public List<PcbImageVM> getImages() {
        return images;
    }

    public void setImages(List<PcbImageVM> images) {
        this.images = images;
    }

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getManagerPhoneNumber() {
        return managerPhoneNumber;
    }

    public void setManagerPhoneNumber(String managerPhoneNumber) {
        this.managerPhoneNumber = managerPhoneNumber;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public String getWatt() {
        return watt;
    }

    public void setWatt(String watt) {
        this.watt = watt;
    }

    public String getTolerance() {
        return tolerance;
    }

    public void setTolerance(String tolerance) {
        this.tolerance = tolerance;
    }

    public String getOhm() {
        return ohm;
    }

    public void setOhm(String ohm) {
        this.ohm = ohm;
    }

    public String getCondenser() {
        return condenser;
    }

    public void setCondenser(String condenser) {
        this.condenser = condenser;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getInductor() {
        return inductor;
    }

    public void setInductor(String inductor) {
        this.inductor = inductor;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<PcbPartSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<PcbPartSpec> specs) {
        this.specs = specs;
    }
}
