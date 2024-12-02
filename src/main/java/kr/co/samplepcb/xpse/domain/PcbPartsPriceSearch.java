package kr.co.samplepcb.xpse.domain;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

public class PcbPartsPriceSearch {

    private String distributor;
    private String sku;
    private int stock;
    private int moq;
    private String pkg;
    @Field(type = FieldType.Nested)
    private List<PcbPartsPriceStepSearch> priceSteps;
    private Date updatedDate;

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getMoq() {
        return moq;
    }

    public void setMoq(int moq) {
        this.moq = moq;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public List<PcbPartsPriceStepSearch> getPriceSteps() {
        return priceSteps;
    }

    public void setPriceSteps(List<PcbPartsPriceStepSearch> priceSteps) {
        this.priceSteps = priceSteps;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
