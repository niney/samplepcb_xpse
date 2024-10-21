package kr.co.samplepcb.xpse.domain;

import kr.co.samplepcb.xpse.pojo.ElasticIndexName;
import kr.co.samplepcb.xpse.pojo.PcbImageVM;
import kr.co.samplepcb.xpse.pojo.PcbPartSpec;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;
import java.util.List;

@Document(indexName = ElasticIndexName.PCB_PARTS)
public class PcbPartsSearch implements Persistable<String> {

    @Id
    private String id;
    @CreatedDate
    private Date writeDate;
    @LastModifiedDate
    private Date lastModifiedDate;
    @Field(type = FieldType.Keyword, normalizer = "keyword_normalizer")
    private String serviceType;
    @Field(type = FieldType.Keyword, normalizer = "keyword_normalizer")
    private String subServiceType;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "normalize", type = FieldType.Keyword, normalizer = "keyword_normalizer")
            }
    )
    private String largeCategory;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "normalize", type = FieldType.Keyword, normalizer = "keyword_normalizer")
            }
    )
    private String mediumCategory;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "normalize", type = FieldType.Keyword, normalizer = "keyword_normalizer")
            }
    )
    private String smallCategory;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ngram_analyzer6_case_insensitive", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "normalize", type = FieldType.Keyword, normalizer = "keyword_normalizer"),
                    @InnerField(suffix = "ngram4", type = FieldType.Text, analyzer = "ngram_analyzer4_case_insensitive")
            }
    )
    private String partName;
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ngram_analyzer4_case_insensitive", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "normalize", type = FieldType.Keyword, normalizer = "keyword_normalizer")
            }
    )
    private String manufacturerName;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ngram_analyzer4_case_insensitive", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "normalize", type = FieldType.Keyword, normalizer = "keyword_normalizer")
            }
    )
    private String partsPackaging;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ngram_analyzer4_case_insensitive", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "normalize", type = FieldType.Keyword, normalizer = "keyword_normalizer")
            }
    )
    private String packaging;
    @Field(type = FieldType.Integer)
    private Integer moq;

    @Field(type = FieldType.Integer)
    private Integer price;
    @Field(type = FieldType.Integer)
    private Integer price1; // 1~9
    @Field(type = FieldType.Integer)
    private Integer price2; // 10 ~99
    @Field(type = FieldType.Integer)
    private Integer price3; // 100~499
    @Field(type = FieldType.Integer)
    private Integer price4; // 500~999
    @Field(type = FieldType.Integer)
    private Integer price5; // 1000~

    @Field(type = FieldType.Integer)
    private Integer inventoryLevel;
    @Field(type = FieldType.Text, analyzer = "nori")
    private String memo;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ngram_analyzer_case_insensitive", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String offerName;
    @Field(type = FieldType.Nested)
    private List<PcbImageVM> images;
    @Field(type = FieldType.Keyword, normalizer = "keyword_normalizer")
    private String dateCode;
    @Field(type = FieldType.Keyword, normalizer = "keyword_normalizer")
    private String memberId;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ngram_analyzer_case_insensitive", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String managerPhoneNumber;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ngram_analyzer_case_insensitive", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String managerName;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ngram_analyzer_case_insensitive", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String managerEmail;
    @Field(type = FieldType.Text, analyzer = "nori")
    private String contents;
    private Integer status;
    @Field(type = FieldType.Object)
    private PcbUnitSearch watt;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "samplepcb_analyzer", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer_case_insensitive", fielddata = true)
            }
    )
    private PcbUnitSearch tolerance;
    @Field(type = FieldType.Object)
    private PcbUnitSearch ohm;
    @Field(type = FieldType.Object)
    private PcbUnitSearch condenser;
    @Field(type = FieldType.Object)
    private PcbUnitSearch voltage;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "samplepcb_analyzer", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer_case_insensitive", fielddata = true)
            }
    )
    private String temperature;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "samplepcb_analyzer", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer_case_insensitive", fielddata = true)
            }
    )
    private String size;
    @Field(type = FieldType.Object)
    private PcbUnitSearch current;
    @Field(type = FieldType.Object)
    private PcbUnitSearch inductor;
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ngram_analyzer_case_insensitive", fielddata = true),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "samplepcb", type = FieldType.Text, analyzer = "samplepcb_analyzer", fielddata = true)
            }
    )
    private String productName;

    @Field(type = FieldType.Nested)
    private List<PcbPartSpec> specs;

    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return this.id == null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(Date writeDate) {
        this.writeDate = writeDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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

    public String getDateCode() {
        return dateCode;
    }

    public void setDateCode(String dateCode) {
        this.dateCode = dateCode;
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

    public PcbUnitSearch getWatt() {
        return watt;
    }

    public void setWatt(PcbUnitSearch watt) {
        this.watt = watt;
    }

    public PcbUnitSearch getTolerance() {
        return tolerance;
    }

    public void setTolerance(PcbUnitSearch tolerance) {
        this.tolerance = tolerance;
    }

    public PcbUnitSearch getOhm() {
        return ohm;
    }

    public void setOhm(PcbUnitSearch ohm) {
        this.ohm = ohm;
    }

    public PcbUnitSearch getCondenser() {
        return condenser;
    }

    public void setCondenser(PcbUnitSearch condenser) {
        this.condenser = condenser;
    }

    public PcbUnitSearch getVoltage() {
        return voltage;
    }

    public void setVoltage(PcbUnitSearch voltage) {
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

    public PcbUnitSearch getCurrent() {
        return current;
    }

    public void setCurrent(PcbUnitSearch current) {
        this.current = current;
    }

    public PcbUnitSearch getInductor() {
        return inductor;
    }

    public void setInductor(PcbUnitSearch inductor) {
        this.inductor = inductor;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<PcbPartSpec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<PcbPartSpec> specs) {
        this.specs = specs;
    }
}
