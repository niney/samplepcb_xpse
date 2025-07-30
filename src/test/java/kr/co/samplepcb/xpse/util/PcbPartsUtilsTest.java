package kr.co.samplepcb.xpse.util;

import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PcbPartsUtilsTest {

    @Test
    public void testParseStringWithoutReferencePrefix() {
        // 기존 parseString(String text) 함수 테스트
        String input = "22pF 100nF 10uF";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input);
        
        List<String> condensers = result.get(PcbPartsSearchField.CONDENSER);
        assertNotNull(condensers);
        assertEquals(3, condensers.size());
        assertTrue(condensers.contains("22pF"));
        assertTrue(condensers.contains("100nF"));
        assertTrue(condensers.contains("10uF"));
    }

    @Test
    public void testParseStringWithShortCapacitorUnits_WithCapacitorPrefix() {
        // referencePrefix가 "C"일 때 축약된 커패시터 단위 인식 테스트
        String input = "22p 33n 10u";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "C");
        
        List<String> condensers = result.get(PcbPartsSearchField.CONDENSER);
        assertNotNull(condensers);
        assertEquals(3, condensers.size());
        assertTrue(condensers.contains("22pF"));
        assertTrue(condensers.contains("33nF"));
        assertTrue(condensers.contains("10uF"));
    }

    @Test
    public void testParseStringWithShortCapacitorUnits_WithoutCapacitorPrefix() {
        // referencePrefix가 "C"가 아닐 때는 축약된 단위를 인식하지 않음
        String input = "22p 33n 10u";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "R");
        
        List<String> condensers = result.get(PcbPartsSearchField.CONDENSER);
        assertTrue(condensers == null || condensers.isEmpty());
        
        // PRODUCT_NAME으로 분류되어야 함
        List<String> productNames = result.get(PcbPartsSearchField.PRODUCT_NAME);
        assertNotNull(productNames);
        assertTrue(productNames.contains("22p"));
        assertTrue(productNames.contains("33n"));
        assertTrue(productNames.contains("10u"));
    }

    @Test
    public void testParseStringWithShortCapacitorUnits_NullPrefix() {
        // referencePrefix가 null일 때는 축약된 단위를 인식하지 않음
        String input = "22p 33n 10u";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, null);
        
        List<String> condensers = result.get(PcbPartsSearchField.CONDENSER);
        assertTrue(condensers == null || condensers.isEmpty());
        
        // PRODUCT_NAME으로 분류되어야 함
        List<String> productNames = result.get(PcbPartsSearchField.PRODUCT_NAME);
        assertNotNull(productNames);
        assertTrue(productNames.contains("22p"));
        assertTrue(productNames.contains("33n"));
        assertTrue(productNames.contains("10u"));
    }

    @Test
    public void testParseStringMixedUnits_WithCapacitorPrefix() {
        // 축약된 단위와 전체 단위가 섞여있는 경우
        String input = "22p 100nF 10u 47pF";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "C");
        
        List<String> condensers = result.get(PcbPartsSearchField.CONDENSER);
        assertNotNull(condensers);
        assertEquals(4, condensers.size());
        assertTrue(condensers.contains("22pF"));
        assertTrue(condensers.contains("100nF"));
        assertTrue(condensers.contains("10uF"));
        assertTrue(condensers.contains("47pF"));
    }

    @Test
    public void testParseStringWithOtherUnits_WithCapacitorPrefix() {
        // 커패시터가 아닌 다른 단위들은 정상적으로 인식되는지 테스트
        String input = "100ohm 5V 22p 1W";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "C");
        
        List<String> ohms = result.get(PcbPartsSearchField.OHM);
        assertNotNull(ohms);
        assertTrue(ohms.contains("100ohm"));
        
        List<String> voltages = result.get(PcbPartsSearchField.VOLTAGE);
        assertNotNull(voltages);
        assertTrue(voltages.contains("5V"));
        
        List<String> watts = result.get(PcbPartsSearchField.WATT);
        assertNotNull(watts);
        assertTrue(watts.contains("1W"));
        
        List<String> condensers = result.get(PcbPartsSearchField.CONDENSER);
        assertNotNull(condensers);
        assertTrue(condensers.contains("22pF"));
    }

    @Test
    public void testParseStringDecimalValues_WithCapacitorPrefix() {
        // 소수점이 포함된 축약된 단위 테스트
        String input = "2.2p 3.3n 1.0u";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "C");
        
        List<String> condensers = result.get(PcbPartsSearchField.CONDENSER);
        assertNotNull(condensers);
        assertEquals(3, condensers.size());
        assertTrue(condensers.contains("2.2pF"));
        assertTrue(condensers.contains("3.3nF"));
        assertTrue(condensers.contains("1.0uF"));
    }

    @Test
    public void testBackwardCompatibility() {
        // 기존 parseString(String) 함수 호환성 테스트
        String input = "22pF 100nF 10uF 100ohm 5V";
        
        Map<String, List<String>> oldResult = PcbPartsUtils.parseString(input);
        Map<String, List<String>> newResult = PcbPartsUtils.parseString(input, null);
        
        // 결과가 동일해야 함
        assertEquals(oldResult.get(PcbPartsSearchField.CONDENSER), newResult.get(PcbPartsSearchField.CONDENSER));
        assertEquals(oldResult.get(PcbPartsSearchField.OHM), newResult.get(PcbPartsSearchField.OHM));
        assertEquals(oldResult.get(PcbPartsSearchField.VOLTAGE), newResult.get(PcbPartsSearchField.VOLTAGE));
        assertEquals(oldResult.get(PcbPartsSearchField.PRODUCT_NAME), newResult.get(PcbPartsSearchField.PRODUCT_NAME));
    }

    // ===== 저항(OHM) 단위 테스트 =====
    
    @Test
    public void testParseStringWithResistorNotation_WithResistorPrefix() {
        // referencePrefix가 "R"일 때 R 표기법 인식 테스트
        String input = "2R2 1K2 4R7";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "R");
        
        List<String> ohms = result.get(PcbPartsSearchField.OHM);
        assertNotNull(ohms);
        assertEquals(3, ohms.size());
        assertTrue(ohms.contains("2.2ohm"));
        assertTrue(ohms.contains("1.2kohm"));
        assertTrue(ohms.contains("4.7ohm"));
    }

    @Test
    public void testParseStringWithCapitalLetterUnits_WithResistorPrefix() {
        // 대문자 배수 단위 테스트
        String input = "2.2K 1M 4.7G 100R";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "R");
        
        List<String> ohms = result.get(PcbPartsSearchField.OHM);
        assertNotNull(ohms);
        assertEquals(4, ohms.size());
        assertTrue(ohms.contains("2.2kohm"));
        assertTrue(ohms.contains("1mohm"));
        assertTrue(ohms.contains("4.7gohm"));
        assertTrue(ohms.contains("100ohm"));
    }

    @Test
    public void testParseStringWithBasicResistorUnits_WithResistorPrefix() {
        // 기본 저항 단위 테스트
        String input = "4.7R 100Ω 220ohms";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "R");
        
        List<String> ohms = result.get(PcbPartsSearchField.OHM);
        assertNotNull(ohms);
        assertEquals(3, ohms.size());
        assertTrue(ohms.contains("4.7ohm"));
        assertTrue(ohms.contains("100ohm"));
        assertTrue(ohms.contains("220ohm"));  // ohms → ohm으로 정규화됨
    }

    @Test
    public void testParseStringWithMilliMicroOhms_WithResistorPrefix() {
        // 밀리옴, 마이크로옴 단위 테스트
        String input = "50mΩ 100µΩ 75mR 200uR";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "R");
        
        List<String> ohms = result.get(PcbPartsSearchField.OHM);
        assertNotNull(ohms);
        assertEquals(4, ohms.size());
        assertTrue(ohms.contains("50mohm"));
        assertTrue(ohms.contains("100uohm"));
        assertTrue(ohms.contains("75mohm"));
        assertTrue(ohms.contains("200uohm"));
    }

    @Test
    public void testParseStringWithRangeNotation_WithResistorPrefix() {
        // 범위 표기 테스트
        String input = "1k-10k 100Ω~1kΩ";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "R");
        
        List<String> ohms = result.get(PcbPartsSearchField.OHM);
        assertNotNull(ohms);
        assertEquals(2, ohms.size());
        assertTrue(ohms.contains("1kohm-10kohm"));
        assertTrue(ohms.contains("100ohm~1kohm"));
    }

    @Test
    public void testParseStringWithResistorUnits_WithoutResistorPrefix() {
        // referencePrefix가 "R"이 아닐 때는 확장된 단위를 인식하지 않음
        String input = "2R2 1K2 4.7K";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "C");
        
        List<String> ohms = result.get(PcbPartsSearchField.OHM);
        assertTrue(ohms == null || ohms.isEmpty());
        
        // PRODUCT_NAME으로 분류되어야 함
        List<String> productNames = result.get(PcbPartsSearchField.PRODUCT_NAME);
        assertNotNull(productNames);
        assertTrue(productNames.contains("2R2"));
        assertTrue(productNames.contains("1K2"));
        assertTrue(productNames.contains("4.7K"));
    }

    @Test
    public void testParseStringWithResistorUnits_NullPrefix() {
        // referencePrefix가 null일 때는 확장된 단위를 인식하지 않음
        String input = "2R2 1K2 4.7K";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, null);
        
        List<String> ohms = result.get(PcbPartsSearchField.OHM);
        assertTrue(ohms == null || ohms.isEmpty());
        
        // PRODUCT_NAME으로 분류되어야 함
        List<String> productNames = result.get(PcbPartsSearchField.PRODUCT_NAME);
        assertNotNull(productNames);
        assertTrue(productNames.contains("2R2"));
        assertTrue(productNames.contains("1K2"));
        assertTrue(productNames.contains("4.7K"));
    }

    @Test
    public void testParseStringWithMixedUnits_WithResistorPrefix() {
        // 저항과 다른 단위가 섞여있는 경우
        String input = "2R2 22pF 5V 1K2";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "R");
        
        List<String> ohms = result.get(PcbPartsSearchField.OHM);
        assertNotNull(ohms);
        assertEquals(2, ohms.size());
        assertTrue(ohms.contains("2.2ohm"));
        assertTrue(ohms.contains("1.2kohm"));
        
        List<String> voltages = result.get(PcbPartsSearchField.VOLTAGE);
        assertNotNull(voltages);
        assertTrue(voltages.contains("5V"));
        
        List<String> condensers = result.get(PcbPartsSearchField.CONDENSER);
        assertNotNull(condensers);
        assertTrue(condensers.contains("22pF"));
    }

    @Test
    public void testParseStringComplexResistorNotation_WithResistorPrefix() {
        // 복잡한 R 표기법 테스트
        String input = "2M2 1G5 10K0";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, "R");
        
        List<String> ohms = result.get(PcbPartsSearchField.OHM);
        assertNotNull(ohms);
        assertEquals(3, ohms.size());
        assertTrue(ohms.contains("2.2mohm"));
        assertTrue(ohms.contains("1.5gohm"));
        assertTrue(ohms.contains("10.0kohm"));
    }
} 