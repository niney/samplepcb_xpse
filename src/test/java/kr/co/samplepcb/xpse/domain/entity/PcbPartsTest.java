package kr.co.samplepcb.xpse.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PcbPartsTest {

    private PcbParts pcbParts;

    @BeforeEach
    void setUp() {
        pcbParts = new PcbParts();
        pcbParts.setId(1L);
        pcbParts.setWriteDate(new Date());
        pcbParts.setLastModifiedDate(new Date());
        pcbParts.setServiceType("TYPE_A");
        pcbParts.setPartName("RC0402FR-0710KL");
        pcbParts.setManufacturerName("YAGEO");
    }

    @Test
    void testBasicFields() {
        assertEquals(1L, pcbParts.getId());
        assertEquals("TYPE_A", pcbParts.getServiceType());
        assertEquals("RC0402FR-0710KL", pcbParts.getPartName());
        assertEquals("YAGEO", pcbParts.getManufacturerName());
        assertNotNull(pcbParts.getWriteDate());
        assertNotNull(pcbParts.getLastModifiedDate());
    }

    @Test
    void testAllScalarFields() {
        pcbParts.setSubServiceType("SUB_A");
        pcbParts.setLargeCategory("저항");
        pcbParts.setMediumCategory("칩저항");
        pcbParts.setSmallCategory("일반칩저항");
        pcbParts.setDescription("Resistor 10K");
        pcbParts.setPartsPackaging("Tape & Reel");
        pcbParts.setMoq(100);
        pcbParts.setPrice(50);
        pcbParts.setMemo("TEST MEMO");
        pcbParts.setOfferName("OFFER_1");
        pcbParts.setDateCode("2024");
        pcbParts.setMemberId("member-001");
        pcbParts.setManagerPhoneNumber("010-1234-5678");
        pcbParts.setManagerName("홍길동");
        pcbParts.setManagerEmail("test@example.com");
        pcbParts.setContents("contents");
        pcbParts.setStatus(1);
        pcbParts.setTemperature("-40°C to 85°C");
        pcbParts.setSize("0402");
        pcbParts.setProductName("PRODUCT_1");
        pcbParts.setPhotoUrl("http://example.com/photo.jpg");
        pcbParts.setDatasheetUrl("http://example.com/datasheet.pdf");

        assertEquals("SUB_A", pcbParts.getSubServiceType());
        assertEquals("저항", pcbParts.getLargeCategory());
        assertEquals("칩저항", pcbParts.getMediumCategory());
        assertEquals("일반칩저항", pcbParts.getSmallCategory());
        assertEquals("Resistor 10K", pcbParts.getDescription());
        assertEquals("Tape & Reel", pcbParts.getPartsPackaging());
        assertEquals(100, pcbParts.getMoq());
        assertEquals(50, pcbParts.getPrice());
        assertEquals("TEST MEMO", pcbParts.getMemo());
        assertEquals("OFFER_1", pcbParts.getOfferName());
        assertEquals("2024", pcbParts.getDateCode());
        assertEquals("member-001", pcbParts.getMemberId());
        assertEquals("010-1234-5678", pcbParts.getManagerPhoneNumber());
        assertEquals("홍길동", pcbParts.getManagerName());
        assertEquals("test@example.com", pcbParts.getManagerEmail());
        assertEquals("contents", pcbParts.getContents());
        assertEquals(1, pcbParts.getStatus());
        assertEquals("-40°C to 85°C", pcbParts.getTemperature());
        assertEquals("0402", pcbParts.getSize());
        assertEquals("PRODUCT_1", pcbParts.getProductName());
        assertEquals("http://example.com/photo.jpg", pcbParts.getPhotoUrl());
        assertEquals("http://example.com/datasheet.pdf", pcbParts.getDatasheetUrl());
    }

    @Test
    void testJsonTextFields() {
        String json = "{\"field1\":\"5V\",\"field2\":\"DC\"}";
        pcbParts.setWatt(json);
        pcbParts.setTolerance(json);
        pcbParts.setOhm(json);
        pcbParts.setCondenser(json);
        pcbParts.setVoltage(json);
        pcbParts.setCurrentVal(json);
        pcbParts.setInductor(json);
        pcbParts.setPackaging(json);

        assertEquals(json, pcbParts.getWatt());
        assertEquals(json, pcbParts.getTolerance());
        assertEquals(json, pcbParts.getOhm());
        assertEquals(json, pcbParts.getCondenser());
        assertEquals(json, pcbParts.getVoltage());
        assertEquals(json, pcbParts.getCurrentVal());
        assertEquals(json, pcbParts.getInductor());
        assertEquals(json, pcbParts.getPackaging());
    }

    @Test
    void testAddPrice_CascadeRelationship() {
        PcbPartsPrice price = new PcbPartsPrice();
        price.setDistributor("DigiKey");
        price.setSku("296-1234-1-ND");
        price.setStock(500);
        price.setMoq(1);
        price.setPcbParts(pcbParts);

        pcbParts.getPrices().add(price);

        assertEquals(1, pcbParts.getPrices().size());
        assertEquals("DigiKey", pcbParts.getPrices().get(0).getDistributor());
        assertSame(pcbParts, pcbParts.getPrices().get(0).getPcbParts());
    }

    @Test
    void testRemovePrice_OrphanRemoval() {
        PcbPartsPrice price1 = new PcbPartsPrice();
        price1.setDistributor("DigiKey");
        price1.setPcbParts(pcbParts);

        PcbPartsPrice price2 = new PcbPartsPrice();
        price2.setDistributor("Mouser");
        price2.setPcbParts(pcbParts);

        pcbParts.getPrices().add(price1);
        pcbParts.getPrices().add(price2);
        assertEquals(2, pcbParts.getPrices().size());

        pcbParts.getPrices().remove(price1);
        assertEquals(1, pcbParts.getPrices().size());
        assertEquals("Mouser", pcbParts.getPrices().get(0).getDistributor());
    }

    @Test
    void testAddImage_CascadeRelationship() {
        PcbPartsImage image = new PcbPartsImage();
        image.setUploadFileName("uploaded_123.jpg");
        image.setOriginFileName("photo.jpg");
        image.setPathToken("abc/def");
        image.setSize("1024");
        image.setPcbParts(pcbParts);

        pcbParts.getImages().add(image);

        assertEquals(1, pcbParts.getImages().size());
        assertEquals("uploaded_123.jpg", pcbParts.getImages().get(0).getUploadFileName());
        assertEquals("photo.jpg", pcbParts.getImages().get(0).getOriginFileName());
        assertSame(pcbParts, pcbParts.getImages().get(0).getPcbParts());
    }

    @Test
    void testAddSpec_CascadeRelationship() {
        PcbPartsSpec spec = new PcbPartsSpec();
        spec.setDisplayValue("10kΩ");
        spec.setAttrGroup("Resistance");
        spec.setAttrName("Resistance");
        spec.setAttrShortname("R");
        spec.setPcbParts(pcbParts);

        pcbParts.getSpecs().add(spec);

        assertEquals(1, pcbParts.getSpecs().size());
        assertEquals("10kΩ", pcbParts.getSpecs().get(0).getDisplayValue());
        assertEquals("Resistance", pcbParts.getSpecs().get(0).getAttrGroup());
        assertSame(pcbParts, pcbParts.getSpecs().get(0).getPcbParts());
    }

    @Test
    void testPriceWithPriceSteps_NestedCascade() {
        PcbPartsPrice price = new PcbPartsPrice();
        price.setDistributor("DigiKey");
        price.setSku("296-1234-1-ND");
        price.setStock(1000);
        price.setMoq(1);
        price.setPkg("Tape & Reel");
        price.setUpdatedDate(new Date());
        price.setPcbParts(pcbParts);

        PcbPartsPriceStep step1 = new PcbPartsPriceStep();
        step1.setBreakQuantity(1);
        step1.setUnitPrice(100);
        step1.setPcbPartsPrice(price);

        PcbPartsPriceStep step2 = new PcbPartsPriceStep();
        step2.setBreakQuantity(100);
        step2.setUnitPrice(80);
        step2.setPcbPartsPrice(price);

        PcbPartsPriceStep step3 = new PcbPartsPriceStep();
        step3.setBreakQuantity(1000);
        step3.setUnitPrice(50);
        step3.setPcbPartsPrice(price);

        price.getPriceSteps().add(step1);
        price.getPriceSteps().add(step2);
        price.getPriceSteps().add(step3);

        pcbParts.getPrices().add(price);

        // pcb_parts -> pcb_parts_price -> pcb_parts_price_step 3단계 관계
        assertEquals(1, pcbParts.getPrices().size());
        assertEquals(3, pcbParts.getPrices().get(0).getPriceSteps().size());

        PcbPartsPriceStep firstStep = pcbParts.getPrices().get(0).getPriceSteps().get(0);
        assertEquals(1, firstStep.getBreakQuantity());
        assertEquals(100, firstStep.getUnitPrice());
        assertSame(price, firstStep.getPcbPartsPrice());
    }

    @Test
    void testMultipleDistributorPrices() {
        PcbPartsPrice digikeyPrice = new PcbPartsPrice();
        digikeyPrice.setDistributor("DigiKey");
        digikeyPrice.setSku("296-1234-1-ND");
        digikeyPrice.setStock(500);
        digikeyPrice.setMoq(1);
        digikeyPrice.setPcbParts(pcbParts);

        PcbPartsPriceStep dkStep = new PcbPartsPriceStep();
        dkStep.setBreakQuantity(1);
        dkStep.setUnitPrice(120);
        dkStep.setPcbPartsPrice(digikeyPrice);
        digikeyPrice.getPriceSteps().add(dkStep);

        PcbPartsPrice mouserPrice = new PcbPartsPrice();
        mouserPrice.setDistributor("Mouser");
        mouserPrice.setSku("603-RC0402FR-0710KL");
        mouserPrice.setStock(2000);
        mouserPrice.setMoq(10);
        mouserPrice.setPcbParts(pcbParts);

        PcbPartsPriceStep mouserStep = new PcbPartsPriceStep();
        mouserStep.setBreakQuantity(1);
        mouserStep.setUnitPrice(90);
        mouserStep.setPcbPartsPrice(mouserPrice);
        mouserPrice.getPriceSteps().add(mouserStep);

        pcbParts.getPrices().add(digikeyPrice);
        pcbParts.getPrices().add(mouserPrice);

        assertEquals(2, pcbParts.getPrices().size());

        // 최저가 계산 (ES의 setPrices 로직과 동일 개념)
        int minPrice = pcbParts.getPrices().stream()
                .flatMap(p -> p.getPriceSteps().stream())
                .filter(s -> s.getBreakQuantity() == 1)
                .mapToInt(PcbPartsPriceStep::getUnitPrice)
                .min()
                .orElse(0);
        assertEquals(90, minPrice);
    }

    @Test
    void testEmptyCollectionsInitialized() {
        PcbParts newParts = new PcbParts();
        assertNotNull(newParts.getPrices());
        assertNotNull(newParts.getImages());
        assertNotNull(newParts.getSpecs());
        assertTrue(newParts.getPrices().isEmpty());
        assertTrue(newParts.getImages().isEmpty());
        assertTrue(newParts.getSpecs().isEmpty());
    }

    @Test
    void testNullableFields() {
        PcbParts newParts = new PcbParts();
        assertNull(newParts.getId());
        assertNull(newParts.getServiceType());
        assertNull(newParts.getPartName());
        assertNull(newParts.getPrice());
        assertNull(newParts.getMoq());
        assertNull(newParts.getStatus());
        assertNull(newParts.getWatt());
        assertNull(newParts.getVoltage());
    }
}
