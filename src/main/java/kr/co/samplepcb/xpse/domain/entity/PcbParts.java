package kr.co.samplepcb.xpse.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sp_pcb_parts")
public class PcbParts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "doc_id", length = 20, nullable = false, unique = true)
    private String docId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "write_date", nullable = false)
    private Date writeDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date", nullable = false)
    private Date lastModifiedDate;

    @Column(name = "service_type", length = 100)
    private String serviceType;

    @Column(name = "sub_service_type", length = 100)
    private String subServiceType;

    @Column(name = "large_category", length = 255)
    private String largeCategory;

    @Column(name = "medium_category", length = 255)
    private String mediumCategory;

    @Column(name = "small_category", length = 255)
    private String smallCategory;

    @Column(name = "part_name", length = 255)
    private String partName;

    @Lob
    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "manufacturer_name", length = 255)
    private String manufacturerName;

    @Column(name = "parts_packaging", length = 255)
    private String partsPackaging;

    @Lob
    @Column(name = "packaging", columnDefinition = "text")
    private String packaging;

    @Column(name = "moq")
    private Integer moq;

    @Column(name = "price")
    private Integer price;

    @Lob
    @Column(name = "memo", columnDefinition = "text")
    private String memo;

    @Column(name = "offer_name", length = 255)
    private String offerName;

    @Column(name = "date_code", length = 100)
    private String dateCode;

    @Column(name = "member_id", length = 255)
    private String memberId;

    @Column(name = "manager_phone_number", length = 50)
    private String managerPhoneNumber;

    @Column(name = "manager_name", length = 255)
    private String managerName;

    @Column(name = "manager_email", length = 255)
    private String managerEmail;

    @Lob
    @Column(name = "contents", columnDefinition = "text")
    private String contents;

    @Column(name = "status")
    private Integer status;

    @Lob
    @Column(name = "watt", columnDefinition = "text")
    private String watt;

    @Lob
    @Column(name = "tolerance", columnDefinition = "text")
    private String tolerance;

    @Lob
    @Column(name = "ohm", columnDefinition = "text")
    private String ohm;

    @Lob
    @Column(name = "condenser", columnDefinition = "text")
    private String condenser;

    @Lob
    @Column(name = "voltage", columnDefinition = "text")
    private String voltage;

    @Column(name = "temperature", length = 255)
    private String temperature;

    @Column(name = "size", length = 255)
    private String size;

    @Lob
    @Column(name = "current_val", columnDefinition = "text")
    private String currentVal;

    @Lob
    @Column(name = "inductor", columnDefinition = "text")
    private String inductor;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Lob
    @Column(name = "photo_url", columnDefinition = "text")
    private String photoUrl;

    @Lob
    @Column(name = "datasheet_url", columnDefinition = "text")
    private String datasheetUrl;

    @OneToMany(mappedBy = "pcbParts", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PcbPartsPrice> prices = new ArrayList<>();

    @OneToMany(mappedBy = "pcbParts", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PcbPartsImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "pcbParts", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PcbPartsSpec> specs = new ArrayList<>();

    // === getter / setter ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }

    public Date getWriteDate() { return writeDate; }
    public void setWriteDate(Date writeDate) { this.writeDate = writeDate; }

    public Date getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(Date lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getSubServiceType() { return subServiceType; }
    public void setSubServiceType(String subServiceType) { this.subServiceType = subServiceType; }

    public String getLargeCategory() { return largeCategory; }
    public void setLargeCategory(String largeCategory) { this.largeCategory = largeCategory; }

    public String getMediumCategory() { return mediumCategory; }
    public void setMediumCategory(String mediumCategory) { this.mediumCategory = mediumCategory; }

    public String getSmallCategory() { return smallCategory; }
    public void setSmallCategory(String smallCategory) { this.smallCategory = smallCategory; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }

    public String getPartsPackaging() { return partsPackaging; }
    public void setPartsPackaging(String partsPackaging) { this.partsPackaging = partsPackaging; }

    public String getPackaging() { return packaging; }
    public void setPackaging(String packaging) { this.packaging = packaging; }

    public Integer getMoq() { return moq; }
    public void setMoq(Integer moq) { this.moq = moq; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public String getOfferName() { return offerName; }
    public void setOfferName(String offerName) { this.offerName = offerName; }

    public String getDateCode() { return dateCode; }
    public void setDateCode(String dateCode) { this.dateCode = dateCode; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getManagerPhoneNumber() { return managerPhoneNumber; }
    public void setManagerPhoneNumber(String managerPhoneNumber) { this.managerPhoneNumber = managerPhoneNumber; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }

    public String getContents() { return contents; }
    public void setContents(String contents) { this.contents = contents; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getWatt() { return watt; }
    public void setWatt(String watt) { this.watt = watt; }

    public String getTolerance() { return tolerance; }
    public void setTolerance(String tolerance) { this.tolerance = tolerance; }

    public String getOhm() { return ohm; }
    public void setOhm(String ohm) { this.ohm = ohm; }

    public String getCondenser() { return condenser; }
    public void setCondenser(String condenser) { this.condenser = condenser; }

    public String getVoltage() { return voltage; }
    public void setVoltage(String voltage) { this.voltage = voltage; }

    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getCurrentVal() { return currentVal; }
    public void setCurrentVal(String currentVal) { this.currentVal = currentVal; }

    public String getInductor() { return inductor; }
    public void setInductor(String inductor) { this.inductor = inductor; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getDatasheetUrl() { return datasheetUrl; }
    public void setDatasheetUrl(String datasheetUrl) { this.datasheetUrl = datasheetUrl; }

    public List<PcbPartsPrice> getPrices() { return prices; }
    public void setPrices(List<PcbPartsPrice> prices) { this.prices = prices; }

    public List<PcbPartsImage> getImages() { return images; }
    public void setImages(List<PcbPartsImage> images) { this.images = images; }

    public List<PcbPartsSpec> getSpecs() { return specs; }
    public void setSpecs(List<PcbPartsSpec> specs) { this.specs = specs; }
}
