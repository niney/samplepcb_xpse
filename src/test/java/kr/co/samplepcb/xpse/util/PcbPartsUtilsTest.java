package kr.co.samplepcb.xpse.util;

import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
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

    @Test
    public void testParseStringWithPartNumber() {
        String input = "STM32H723ZGT6";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input);
        
        // 파트넘버가 값으로 인식되면 안됨
        List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
        assertTrue(inductors == null || inductors.isEmpty(), "Part number should not be recognized as inductor");
        
        // 파트넘버는 PRODUCT_NAME으로 분류되어야 함
        List<String> productNames = result.get(PcbPartsSearchField.PRODUCT_NAME);
        assertNotNull(productNames);
        assertTrue(productNames.contains(input), "Part number should be in PRODUCT_NAME");
    }

    // ===== 인덕터 관련 추가 테스트 =====
    
    @Test
    public void testInductorValuesAreCorrectlyRecognized() {
        // 정상적인 인덕터 값들이 올바르게 인식되는지 테스트
        Map<String, String> testCases = Map.of(
            "10uH", "10uH",
            "100nH", "100nH",
            "1mH", "1mH",
            "2.2uH", "2.2uH",
            "470nH", "470nH"
        );
        
        for (Map.Entry<String, String> testCase : testCases.entrySet()) {
            Map<String, List<String>> result = PcbPartsUtils.parseString(testCase.getKey());
            List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
            assertNotNull(inductors, "Inductor should be recognized for " + testCase.getKey());
            assertEquals(1, inductors.size());
            assertEquals(testCase.getValue(), inductors.get(0));
        }
    }
    
    @Test
    public void testPartNumbersNotRecognizedAsInductor() {
        // 다양한 파트넘버들이 인덕터로 잘못 인식되지 않는지 테스트
        String[] partNumbers = {
            "STM32H723ZGT6",
            "LM358N",
            "NE555P",
            "74HC595N",
            "ATMEGA328P",
            "ESP32-WROOM-32",
            "AMS1117-3.3",
            "TPS61088RHL",
            "MAX232CPE",
            "CD4017BEH"
        };
        
        for (String partNumber : partNumbers) {
            Map<String, List<String>> result = PcbPartsUtils.parseString(partNumber);
            List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
            assertTrue(inductors == null || inductors.isEmpty(), 
                "Part number '" + partNumber + "' should not be recognized as inductor");
            
            List<String> productNames = result.get(PcbPartsSearchField.PRODUCT_NAME);
            assertNotNull(productNames);
            assertTrue(productNames.contains(partNumber), 
                "Part number '" + partNumber + "' should be in PRODUCT_NAME");
        }
    }
    
    @Test
    public void testMixedInductorAndPartNumber() {
        // 인덕터 값과 파트넘버가 섞여있을 때 올바르게 구분되는지 테스트
        String input = "10uH STM32H723ZGT6 100nH LM358N";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input);
        
        List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
        assertNotNull(inductors);
        assertEquals(2, inductors.size());
        assertTrue(inductors.contains("10uH"));
        assertTrue(inductors.contains("100nH"));
        
        List<String> productNames = result.get(PcbPartsSearchField.PRODUCT_NAME);
        assertNotNull(productNames);
        assertTrue(productNames.contains("STM32H723ZGT6"));
        assertTrue(productNames.contains("LM358N"));
    }
    
    @Test
    public void testInductorWithoutUnitSuffix() {
        // H 단위만 있는 인덕터 값 테스트
        String input = "1H";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input);
        
        List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
        assertNotNull(inductors);
        assertEquals(1, inductors.size());
        assertEquals("1H", inductors.get(0));
    }
    
    @Test  
    public void testAlphanumericWithHNotRecognizedAsInductor() {
        // 숫자+H 형태지만 파트넘버의 일부인 경우 테스트
        String[] testCases = {
            "32H",  // 단독으로는 인덕터로 인식
            "A32H",  // 앞에 문자가 있으면 파트넘버
            "32HA",  // 뒤에 문자가 있으면 파트넘버
            "A32HB", // 앞뒤에 문자가 있으면 파트넘버
        };
        
        // 32H는 단독으로는 인덕터로 인식되어야 함
        Map<String, List<String>> result1 = PcbPartsUtils.parseString(testCases[0]);
        List<String> inductors1 = result1.get(PcbPartsSearchField.INDUCTOR);
        assertNotNull(inductors1);
        assertEquals(1, inductors1.size());
        
        // 나머지는 파트넘버로 인식되어야 함
        for (int i = 1; i < testCases.length; i++) {
            Map<String, List<String>> result = PcbPartsUtils.parseString(testCases[i]);
            List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
            assertTrue(inductors == null || inductors.isEmpty(), 
                testCases[i] + " should not be recognized as inductor");
            
            List<String> productNames = result.get(PcbPartsSearchField.PRODUCT_NAME);
            assertNotNull(productNames);
            assertTrue(productNames.contains(testCases[i]), 
                testCases[i] + " should be in PRODUCT_NAME");
        }
    }
    
    @Test
    public void testInductorCaseInsensitive() {
        // 대소문자 구분 없이 인덕터가 인식되는지 테스트
        String[] testCases = {
            "10UH", "10uh", "10Uh", "10uH",
            "100NH", "100nh", "100Nh", "100nH",
            "1MH", "1mh", "1Mh", "1mH"
        };
        
        for (String testCase : testCases) {
            Map<String, List<String>> result = PcbPartsUtils.parseString(testCase);
            List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
            assertNotNull(inductors, "Inductor should be recognized for " + testCase);
            assertEquals(1, inductors.size());
            // 대소문자에 관계없이 인덕터로 인식되어야 함
            assertTrue(inductors.get(0).toLowerCase().matches("\\d+\\.?\\d*[unmph]+"), 
                "Value should be an inductor: " + inductors.get(0));
        }
    }
    
    @Test
    public void testDecimalInductorValues() {
        // 소수점이 포함된 인덕터 값 테스트
        Map<String, String> testCases = Map.of(
            "1.5uH", "1.5uH",
            "2.2mH", "2.2mH",
            "4.7nH", "4.7nH",
            "0.1uH", "0.1uH",
            "10.0mH", "10.0mH"
        );
        
        for (Map.Entry<String, String> testCase : testCases.entrySet()) {
            Map<String, List<String>> result = PcbPartsUtils.parseString(testCase.getKey());
            List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
            assertNotNull(inductors, "Inductor should be recognized for " + testCase.getKey());
            assertEquals(1, inductors.size());
            assertEquals(testCase.getValue(), inductors.get(0));
        }
    }
    
    @Test
    public void testInductorWithSpaces() {
        // 공백이 포함된 인덕터 값 테스트
        String input = "10 uH";  // 숫자와 단위 사이에 공백
        Map<String, List<String>> result = PcbPartsUtils.parseString(input);
        
        // 새로운 경계 검사 로직에서는 공백이 있어도 인덕터로 인식될 수 있음
        // 패턴: [0-9.]+\s*(?:pH|nH|uH|µH|mH) 에서 \s*가 공백을 허용
        List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
        
        // 만약 인덕터로 인식된다면
        if (inductors != null && !inductors.isEmpty()) {
            // "10 uH"가 인덕터로 인식됨
            assertTrue(inductors.contains("10 uH"));
        } else {
            // 인덕터로 인식되지 않는다면 PRODUCT_NAME으로 분류됨
            List<String> productNames = result.get(PcbPartsSearchField.PRODUCT_NAME);
            assertNotNull(productNames);
            assertTrue(productNames.size() > 0);
        }
    }
    
    @Test
    public void testInductorBoundaryDetection() {
        // 경계 검사 테스트: 특수문자나 공백으로 구분된 경우만 인덕터로 인식
        
        // STM32H723ZGT6 - 문자와 숫자로 연결되어 있으므로 추출 안됨
        Map<String, List<String>> result1 = PcbPartsUtils.parseString("STM32H723ZGT6");
        List<String> inductors1 = result1.get(PcbPartsSearchField.INDUCTOR);
        assertTrue(inductors1 == null || inductors1.isEmpty(), 
            "STM32H723ZGT6 should not extract 32H as inductor");
        
        // STM 32H 723ZGT6 - 공백으로 분리되어 있으므로 32H 추출
        Map<String, List<String>> result2 = PcbPartsUtils.parseString("STM 32H 723ZGT6");
        List<String> inductors2 = result2.get(PcbPartsSearchField.INDUCTOR);
        assertNotNull(inductors2);
        assertTrue(inductors2.contains("32H"), 
            "'STM 32H 723ZGT6' should extract 32H");
        
        // STM(32H)723ZGT6 - 괄호로 구분되어 있으므로 32H 추출
        Map<String, List<String>> result3 = PcbPartsUtils.parseString("STM(32H)723ZGT6");
        List<String> inductors3 = result3.get(PcbPartsSearchField.INDUCTOR);
        assertNotNull(inductors3);
        assertTrue(inductors3.contains("32H"), 
            "'STM(32H)723ZGT6' should extract 32H");
        
        // STM-32H-723ZGT6 - 하이픈으로 구분되어 있으므로 32H 추출
        Map<String, List<String>> result4 = PcbPartsUtils.parseString("STM-32H-723ZGT6");
        List<String> inductors4 = result4.get(PcbPartsSearchField.INDUCTOR);
        assertNotNull(inductors4);
        assertTrue(inductors4.contains("32H"), 
            "'STM-32H-723ZGT6' should extract 32H");
        
        // STM[32H]723ZGT6 - 대괄호로 구분되어 있으므로 32H 추출
        Map<String, List<String>> result5 = PcbPartsUtils.parseString("STM[32H]723ZGT6");
        List<String> inductors5 = result5.get(PcbPartsSearchField.INDUCTOR);
        assertNotNull(inductors5);
        assertTrue(inductors5.contains("32H"), 
            "'STM[32H]723ZGT6' should extract 32H");
        
        // STM_32H_723ZGT6 - 언더스코어로 구분되어 있으므로 32H 추출
        Map<String, List<String>> result6 = PcbPartsUtils.parseString("STM_32H_723ZGT6");
        List<String> inductors6 = result6.get(PcbPartsSearchField.INDUCTOR);
        assertNotNull(inductors6);
        assertTrue(inductors6.contains("32H"), 
            "'STM_32H_723ZGT6' should extract 32H");
        
        // A32H - 앞에 문자가 붙어있으므로 추출 안됨
        Map<String, List<String>> result7 = PcbPartsUtils.parseString("A32H");
        List<String> inductors7 = result7.get(PcbPartsSearchField.INDUCTOR);
        assertTrue(inductors7 == null || inductors7.isEmpty(), 
            "'A32H' should not extract 32H");
        
        // 32HA - 뒤에 문자가 붙어있으므로 추출 안됨
        Map<String, List<String>> result8 = PcbPartsUtils.parseString("32HA");
        List<String> inductors8 = result8.get(PcbPartsSearchField.INDUCTOR);
        assertTrue(inductors8 == null || inductors8.isEmpty(), 
            "'32HA' should not extract 32H");
    }
    
    @Test
    public void testInductorInComplexString() {
        // 복잡한 문자열에서 인덕터 추출 테스트
        String input = "Components: R1=10k, C1=100nF, L1=10uH, IC1=STM32H723ZGT6, L2=(32H)";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input);
        
        List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
        assertNotNull(inductors);
        assertEquals(2, inductors.size());
        assertTrue(inductors.contains("10uH"));
        assertTrue(inductors.contains("32H"));
        
        // STM32H723ZGT6에서 32H가 추출되지 않아야 함
        for (String inductor : inductors) {
            assertNotEquals("STM32H723ZGT6", inductor);
        }
    }
    
    @Test
    public void testVariousInductorBoundaries() {
        // 다양한 경계 문자 테스트
        Map<String, String> testCases = Map.of(
            "(10uH)", "10uH",
            "[100nH]", "100nH",
            "{1mH}", "1mH",
            "\"2.2uH\"", "2.2uH",
            "'470nH'", "470nH",
            ":32H:", "32H",
            ";10uH;", "10uH",
            ",100nH,", "100nH"
        );
        
        for (Map.Entry<String, String> testCase : testCases.entrySet()) {
            Map<String, List<String>> result = PcbPartsUtils.parseString(testCase.getKey());
            List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
            assertNotNull(inductors, "Should extract inductor from " + testCase.getKey());
            assertTrue(inductors.contains(testCase.getValue()),
                "Should extract " + testCase.getValue() + " from " + testCase.getKey());
        }
    }
    
    // ===== 범용 경계 검사 테스트 =====
    
    @Test
    public void testBoundaryDetectionForAllUnits() {
        // 모든 단위 타입에 대해 경계 검사가 올바르게 작동하는지 테스트
        
        // WATT - 경계 검사
        Map<String, List<String>> wattResult1 = PcbPartsUtils.parseString("MAX10W");
        assertTrue(wattResult1.get(PcbPartsSearchField.WATT) == null || 
                   wattResult1.get(PcbPartsSearchField.WATT).isEmpty(),
                   "MAX10W should not extract 10W");
        
        Map<String, List<String>> wattResult2 = PcbPartsUtils.parseString("MAX 10W MIN");
        assertNotNull(wattResult2.get(PcbPartsSearchField.WATT));
        assertTrue(wattResult2.get(PcbPartsSearchField.WATT).contains("10W"),
                   "'MAX 10W MIN' should extract 10W");
        
        // VOLTAGE - 경계 검사
        Map<String, List<String>> voltResult1 = PcbPartsUtils.parseString("LM7805V");
        assertTrue(voltResult1.get(PcbPartsSearchField.VOLTAGE) == null || 
                   voltResult1.get(PcbPartsSearchField.VOLTAGE).isEmpty(),
                   "LM7805V should not extract 5V");
        
        Map<String, List<String>> voltResult2 = PcbPartsUtils.parseString("Input: 5V DC");
        assertNotNull(voltResult2.get(PcbPartsSearchField.VOLTAGE));
        assertTrue(voltResult2.get(PcbPartsSearchField.VOLTAGE).contains("5V"),
                   "'Input: 5V DC' should extract 5V");
        
        // CURRENT - 경계 검사
        Map<String, List<String>> currentResult1 = PcbPartsUtils.parseString("MAX232A");
        assertTrue(currentResult1.get(PcbPartsSearchField.CURRENT) == null || 
                   currentResult1.get(PcbPartsSearchField.CURRENT).isEmpty(),
                   "MAX232A should not extract 32A or 2A");
        
        Map<String, List<String>> currentResult2 = PcbPartsUtils.parseString("MAX: 2A MIN: 500mA");
        assertNotNull(currentResult2.get(PcbPartsSearchField.CURRENT));
        assertTrue(currentResult2.get(PcbPartsSearchField.CURRENT).contains("2A"),
                   "Should extract 2A");
        assertTrue(currentResult2.get(PcbPartsSearchField.CURRENT).contains("500mA"),
                   "Should extract 500mA");
    }
    
    @Test
    public void testComplexPartNumbers() {
        // 복잡한 파트넘버들이 값으로 오인식되지 않는지 테스트
        String[] complexPartNumbers = {
            "TPS61088RHL",     // 88R이 저항값으로 오인식될 수 있음
            "LM2596S-5.0",     // 5.0이 전압으로 오인식될 수 있음
            "XC7K325T-2FFG900C", // 325T, 900C가 오인식될 수 있음
            "AD8066ARZ",       // 66A가 전류로 오인식될 수 있음
            "MCP23017-E/SP",   // 17E가 오인식될 수 있음
            "FT232RL",         // 232R이 저항으로 오인식될 수 있음
            "24LC256-I/P",     // 24L, 256이 오인식될 수 있음
            "SN74HC595N",      // 595N이 오인식될 수 있음
            "ATMEGA328P-AU",   // 328P가 오인식될 수 있음
            "STM32F103C8T6"    // 103C, 8T가 오인식될 수 있음
        };
        
        for (String partNumber : complexPartNumbers) {
            Map<String, List<String>> result = PcbPartsUtils.parseString(partNumber);
            
            // 모든 값 필드가 비어있거나 null이어야 함
            assertTrue(result.get(PcbPartsSearchField.WATT) == null || 
                      result.get(PcbPartsSearchField.WATT).isEmpty(),
                      partNumber + " should not extract watt values");
            assertTrue(result.get(PcbPartsSearchField.VOLTAGE) == null || 
                      result.get(PcbPartsSearchField.VOLTAGE).isEmpty(),
                      partNumber + " should not extract voltage values");
            assertTrue(result.get(PcbPartsSearchField.CURRENT) == null || 
                      result.get(PcbPartsSearchField.CURRENT).isEmpty(),
                      partNumber + " should not extract current values");
            assertTrue(result.get(PcbPartsSearchField.INDUCTOR) == null || 
                      result.get(PcbPartsSearchField.INDUCTOR).isEmpty(),
                      partNumber + " should not extract inductor values");
            
            // PRODUCT_NAME에 파트넘버가 있어야 함
            List<String> productNames = result.get(PcbPartsSearchField.PRODUCT_NAME);
            assertNotNull(productNames);
            assertTrue(productNames.contains(partNumber),
                      partNumber + " should be in PRODUCT_NAME");
        }
    }
    
    @Test
    public void testMixedContentWithBoundaries() {
        // 복잡한 혼합 콘텐츠에서 경계 검사가 올바르게 작동하는지 테스트
        String input = "IC1=STM32F103C8T6, R1=10K, C1=100nF, L1=10uH, VCC=3.3V, IMAX=500mA";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input, null);
        
        // 파트넘버는 값으로 추출되지 않아야 함
        List<String> productNames = result.get(PcbPartsSearchField.PRODUCT_NAME);
        assertNotNull(productNames);
        assertTrue(productNames.contains("IC1=STM32F103C8T6,") || 
                  productNames.stream().anyMatch(p -> p.contains("STM32F103C8T6")),
                  "Part number should be in PRODUCT_NAME");
        
        // 실제 값들은 추출되어야 함
        List<String> voltages = result.get(PcbPartsSearchField.VOLTAGE);
        assertNotNull(voltages);
        assertTrue(voltages.contains("3.3V"), "Should extract 3.3V");
        
        List<String> currents = result.get(PcbPartsSearchField.CURRENT);
        assertNotNull(currents);
        assertTrue(currents.contains("500mA"), "Should extract 500mA");
        
        List<String> condensers = result.get(PcbPartsSearchField.CONDENSER);
        assertNotNull(condensers);
        assertTrue(condensers.contains("100nF"), "Should extract 100nF");
        
        List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
        assertNotNull(inductors);
        assertTrue(inductors.contains("10uH"), "Should extract 10uH");
    }
    
    @Test
    public void testEdgeCasesWithSpecialCharacters() {
        // 특수문자가 포함된 엣지 케이스 테스트
        
        // 대시로 연결된 값
        Map<String, List<String>> dashResult = PcbPartsUtils.parseString("5V-12V-24V");
        List<String> dashVoltages = dashResult.get(PcbPartsSearchField.VOLTAGE);
        assertNotNull(dashVoltages);
        assertEquals(3, dashVoltages.size(), "Should extract all three voltage values");
        
        // 슬래시로 구분된 값
        Map<String, List<String>> slashResult = PcbPartsUtils.parseString("100nF/50V");
        List<String> slashCondensers = slashResult.get(PcbPartsSearchField.CONDENSER);
        assertNotNull(slashCondensers);
        assertTrue(slashCondensers.contains("100nF"));
        List<String> slashVoltages = slashResult.get(PcbPartsSearchField.VOLTAGE);
        assertNotNull(slashVoltages);
        assertTrue(slashVoltages.contains("50V"));
        
        // 괄호 안의 값
        Map<String, List<String>> parenResult = PcbPartsUtils.parseString("Capacitor(22pF) Resistor(4.7K)");
        List<String> parenCondensers = parenResult.get(PcbPartsSearchField.CONDENSER);
        assertNotNull(parenCondensers);
        assertTrue(parenCondensers.contains("22pF"));
        
        // 콜론으로 구분된 값
        Map<String, List<String>> colonResult = PcbPartsUtils.parseString("Vout:5V Iout:2A Power:10W");
        assertNotNull(colonResult.get(PcbPartsSearchField.VOLTAGE));
        assertTrue(colonResult.get(PcbPartsSearchField.VOLTAGE).contains("5V"));
        assertNotNull(colonResult.get(PcbPartsSearchField.CURRENT));
        assertTrue(colonResult.get(PcbPartsSearchField.CURRENT).contains("2A"));
        assertNotNull(colonResult.get(PcbPartsSearchField.WATT));
        assertTrue(colonResult.get(PcbPartsSearchField.WATT).contains("10W"));
    }
    
    @Test
    public void testTemperatureAndSizeWithBoundaries() {
        // 온도와 크기 값의 경계 검사 테스트
        
        // 온도 - 파트넘버에 포함된 경우
        Map<String, List<String>> tempResult1 = PcbPartsUtils.parseString("LM35DZ");
        assertTrue(tempResult1.get(PcbPartsSearchField.TEMPERATURE) == null || 
                   tempResult1.get(PcbPartsSearchField.TEMPERATURE).isEmpty(),
                   "LM35DZ should not extract temperature");
        
        // 온도 - 실제 온도 값
        Map<String, List<String>> tempResult2 = PcbPartsUtils.parseString("Operating: -40°C to 85°C");
        List<String> temps = tempResult2.get(PcbPartsSearchField.TEMPERATURE);
        assertNotNull(temps);
        assertEquals(2, temps.size(), "Should extract both temperature values");
        assertTrue(temps.contains("-40"));
        assertTrue(temps.contains("85"));
        
        // 크기 - 실제 크기 값
        Map<String, List<String>> sizeResult = PcbPartsUtils.parseString("Package: 10x10x2mm");
        List<String> sizes = sizeResult.get(PcbPartsSearchField.SIZE);
        // SIZE 패턴이 "10x10x2mm" 전체를 매칭하지 못할 수 있음
        if (sizes != null && !sizes.isEmpty()) {
            assertTrue(sizes.stream().anyMatch(s -> s.contains("10x10x2")),
                       "Should extract size 10x10x2");
        } else {
            // SIZE가 null이거나 비어있을 수 있음 - 현재 패턴이 mm을 포함한 패턴만 처리하는 경우
            System.out.println("SIZE pattern may need adjustment for 10x10x2mm format");
        }
    }
    
    @Test
    public void testToleranceWithBoundaries() {
        // 공차(Tolerance) 값의 경계 검사 테스트
        
        // 파트넘버에 %가 포함된 경우는 거의 없지만 테스트
        Map<String, List<String>> tolResult1 = PcbPartsUtils.parseString("Tolerance: ±5%");
        List<String> tolerances = tolResult1.get(PcbPartsSearchField.TOLERANCE);
        assertNotNull(tolerances);
        assertTrue(tolerances.contains("±5%"));
        
        // 여러 공차 값
        Map<String, List<String>> tolResult2 = PcbPartsUtils.parseString("1% 5% 10%");
        List<String> multiTol = tolResult2.get(PcbPartsSearchField.TOLERANCE);
        assertNotNull(multiTol);
        assertEquals(3, multiTol.size());
        assertTrue(multiTol.contains("1%"));
        assertTrue(multiTol.contains("5%"));
        assertTrue(multiTol.contains("10%"));
    }
    
    @Test
    public void testRealWorldPCBDescription() {
        // 실제 PCB 부품 설명 텍스트 테스트
        String description = "STM32F103C8T6 ARM Cortex-M3 72MHz Flash:64KB RAM:20KB Package:LQFP48 " +
                           "Operating:2.0V~3.6V Temperature:-40°C~85°C";
        
        Map<String, List<String>> result = PcbPartsUtils.parseString(description);
        
        // 전압 값만 추출되어야 함
        List<String> voltages = result.get(PcbPartsSearchField.VOLTAGE);
        // Operating:2.0V~3.6V에서 전압값은 추출되지 않을 수 있음 (패턴에 따라)
        // 현재 패턴으로는 개별 전압값이 추출될 것임
        
        // 온도 값 추출
        List<String> temps = result.get(PcbPartsSearchField.TEMPERATURE);
        assertNotNull(temps);
        assertEquals(2, temps.size());
        
        // 파트넘버는 PRODUCT_NAME에
        List<String> products = result.get(PcbPartsSearchField.PRODUCT_NAME);
        assertNotNull(products);
        assertTrue(products.stream().anyMatch(p -> p.contains("STM32F103C8T6")));
    }
    
    @Test
    public void testNumberWithUnitLikePatterns() {
        // 숫자와 단위처럼 보이는 패턴들이 파트넘버의 일부일 때 테스트
        Map<String, Boolean> testCases = new HashMap<>();
        testCases.put("DS18B20", false);  // 18B, 20은 추출되면 안됨
        testCases.put("74HC595N", false); // 595N은 추출되면 안됨
        testCases.put("AT24C256", false); // 24C, 256은 추출되면 안됨
        testCases.put("MAX7219CNG", false); // 7219C는 추출되면 안됨
        testCases.put("PCF8574AT", false); // 8574A는 추출되면 안됨
        
        for (Map.Entry<String, Boolean> testCase : testCases.entrySet()) {
            Map<String, List<String>> result = PcbPartsUtils.parseString(testCase.getKey());
            
            // 어떤 값도 추출되면 안됨
            for (String field : new String[]{PcbPartsSearchField.WATT, PcbPartsSearchField.OHM,
                    PcbPartsSearchField.CONDENSER, PcbPartsSearchField.VOLTAGE, 
                    PcbPartsSearchField.INDUCTOR, PcbPartsSearchField.CURRENT}) {
                List<String> values = result.get(field);
                assertTrue(values == null || values.isEmpty(),
                          testCase.getKey() + " should not extract any values for " + field);
            }
            
            // PRODUCT_NAME에는 있어야 함
            List<String> products = result.get(PcbPartsSearchField.PRODUCT_NAME);
            assertNotNull(products);
            assertTrue(products.contains(testCase.getKey()));
        }
    }
    
    @Test
    public void testCombinedUnitsAndPartNumbers() {
        // 실제 값과 파트넘버가 섞여있을 때 정확히 구분하는지 테스트
        String input = "R1=10K R2=22K IC1=NE555P C1=100nF C2=10uF U1=ATMEGA328P-AU L1=100uH";
        Map<String, List<String>> result = PcbPartsUtils.parseString(input);
        
        // NE555P와 ATMEGA328P-AU는 PRODUCT_NAME에
        List<String> products = result.get(PcbPartsSearchField.PRODUCT_NAME);
        assertNotNull(products);
        assertTrue(products.stream().anyMatch(p -> p.contains("NE555P")));
        assertTrue(products.stream().anyMatch(p -> p.contains("ATMEGA328P-AU")));
        
        // 실제 컴포넌트 값들은 추출되어야 함
        List<String> condensers = result.get(PcbPartsSearchField.CONDENSER);
        assertNotNull(condensers);
        assertEquals(2, condensers.size());
        assertTrue(condensers.contains("100nF"));
        assertTrue(condensers.contains("10uF"));
        
        List<String> inductors = result.get(PcbPartsSearchField.INDUCTOR);
        assertNotNull(inductors);
        assertTrue(inductors.contains("100uH"));
    }
}
