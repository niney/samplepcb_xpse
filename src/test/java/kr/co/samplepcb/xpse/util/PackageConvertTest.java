package kr.co.samplepcb.xpse.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static kr.co.samplepcb.xpse.util.PcbPartsUtils.PcbConvert.Unit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PackageConvertTest {

    @Test
    public void test1206Package() {
        PcbPartsUtils.PackageConvert converter = new PcbPartsUtils.PackageConvert();
        Map<Unit, String> result = converter.convert("1206(3216)");

        assertEquals("1206", result.get(Unit.IMPERIAL_SIZE));
        assertEquals("3216", result.get(Unit.METRIC_SIZE));
        assertEquals("12x6", result.get(Unit.IMPERIAL));
        assertEquals("3.2x1.6", result.get(Unit.METRIC));
    }

    @Test
    public void test0603Package() {
        PcbPartsUtils.PackageConvert converter = new PcbPartsUtils.PackageConvert();
        Map<Unit, String> result = converter.convert("0603(1608)");

        assertEquals("0603", result.get(Unit.IMPERIAL_SIZE));
        assertEquals("1608", result.get(Unit.METRIC_SIZE));
        assertEquals("6x3", result.get(Unit.IMPERIAL));
        assertEquals("1.6x0.8", result.get(Unit.METRIC));
    }

    @Test
    public void test0402Package() {
        PcbPartsUtils.PackageConvert converter = new PcbPartsUtils.PackageConvert();
        Map<Unit, String> result = converter.convert("0402(1005)");

        assertEquals("0402", result.get(Unit.IMPERIAL_SIZE));
        assertEquals("1005", result.get(Unit.METRIC_SIZE));
        assertEquals("4x2", result.get(Unit.IMPERIAL));
        assertEquals("1.0x0.5", result.get(Unit.METRIC));
    }

    @Test
    public void test2512Package() {
        PcbPartsUtils.PackageConvert converter = new PcbPartsUtils.PackageConvert();
        Map<Unit, String> result = converter.convert("2512(6332)");

        assertEquals("2512", result.get(Unit.IMPERIAL_SIZE));
        assertEquals("6332", result.get(Unit.METRIC_SIZE));
        assertEquals("25x12", result.get(Unit.IMPERIAL));
        assertEquals("6.3x3.2", result.get(Unit.METRIC));
    }

    @Test
    public void testDifferentFormatting() {
        PcbPartsUtils.PackageConvert converter = new PcbPartsUtils.PackageConvert();

        // 다양한 포맷 테스트
        Map<Unit, String> result1 = converter.convert("1206 (3216)");  // 공백 있는 경우
        Map<Unit, String> result2 = converter.convert("1206( 3216 )"); // 괄호 안 공백
        Map<Unit, String> result3 = converter.convert("1206(3216)");   // 공백 없는 경우

        // 모든 포맷에 대해 동일한 결과를 기대
        String[][] expectedResults = {
                {"1206", "3216", "12x6", "3.2x1.6"},
                {"1206", "3216", "12x6", "3.2x1.6"},
                {"1206", "3216", "12x6", "3.2x1.6"}
        };

        Map<Unit, String>[] results = new Map[]{result1, result2, result3};

        for (int i = 0; i < results.length; i++) {
            assertEquals(expectedResults[i][0], results[i].get(Unit.IMPERIAL_SIZE));
            assertEquals(expectedResults[i][1], results[i].get(Unit.METRIC_SIZE));
            assertEquals(expectedResults[i][2], results[i].get(Unit.IMPERIAL));
            assertEquals(expectedResults[i][3], results[i].get(Unit.METRIC));
        }
    }

    @Test
    public void testInvalidInputs() {
        PcbPartsUtils.PackageConvert converter = new PcbPartsUtils.PackageConvert();

        // 잘못된 입력 테스트
        Map<Unit, String> result1 = converter.convert("12345(3216)");  // 잘못된 임피리얼 코드
        Map<Unit, String> result2 = converter.convert("1206(32167)");  // 잘못된 메트릭 코드
        Map<Unit, String> result3 = converter.convert("abcd(3216)");   // 숫자가 아닌 입력
        Map<Unit, String> result4 = converter.convert("");             // 빈 입력

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        assertTrue(result3.isEmpty());
        assertTrue(result4.isEmpty());
    }

    @Test
    public void testCommonPackageSizes() {
        PcbPartsUtils.PackageConvert converter = new PcbPartsUtils.PackageConvert();

        // 일반적인 패키지 크기들 테스트
        String[][] testCases = {
                // input, imperialSize, metricSize, imperial, metric
                {"0201(0603)", "0201", "0603", "2x1", "0.6x0.3"},
                {"0402(1005)", "0402", "1005", "4x2", "1.0x0.5"},
                {"0603(1608)", "0603", "1608", "6x3", "1.6x0.8"},
                {"0805(2012)", "0805", "2012", "8x5", "2.0x1.2"},
                {"1206(3216)", "1206", "3216", "12x6", "3.2x1.6"},
                {"1210(3225)", "1210", "3225", "12x10", "3.2x2.5"},
                {"2010(5025)", "2010", "5025", "20x10", "5.0x2.5"},
                {"2512(6332)", "2512", "6332", "25x12", "6.3x3.2"}
        };

        for (String[] testCase : testCases) {
            Map<Unit, String> result = converter.convert(testCase[0]);
            assertEquals(testCase[1], result.get(Unit.IMPERIAL_SIZE));
            assertEquals(testCase[2], result.get(Unit.METRIC_SIZE));
            assertEquals(testCase[3], result.get(Unit.IMPERIAL));
            assertEquals(testCase[4], result.get(Unit.METRIC));
        }
    }

    @Test
    public void testCommonPackageConversions() {
        PcbPartsUtils.PackageConvert converter = new PcbPartsUtils.PackageConvert();

        // 정순서 테스트 (imperial to metric)
        testPackage(converter, "0402(1005)", "0402", "1005", "4x2", "1.0x0.5");
        testPackage(converter, "0603(1608)", "0603", "1608", "6x3", "1.6x0.8");
        testPackage(converter, "0805(2012)", "0805", "2012", "8x5", "2.0x1.2");
        testPackage(converter, "1206(3216)", "1206", "3216", "12x6", "3.2x1.6");
        testPackage(converter, "2512(6332)", "2512", "6332", "25x12", "6.3x3.2");

        // 역순서 테스트 (metric to imperial)
        testPackage(converter, "1005(0402)", "0402", "1005", "4x2", "1.0x0.5");
        testPackage(converter, "1608(0603)", "0603", "1608", "6x3", "1.6x0.8");
        testPackage(converter, "2012(0805)", "0805", "2012", "8x5", "2.0x1.2");
        testPackage(converter, "3216(1206)", "1206", "3216", "12x6", "3.2x1.6");
        testPackage(converter, "6332(2512)", "2512", "6332", "25x12", "6.3x3.2");

        testPackage(converter, "1206", "1206", "3216", "12x6", "3.2x1.6");
        testPackage(converter, "0402", "0402", "1005", "4x2", "1.0x0.5");
        testPackage(converter, "2512", "2512", "6332", "25x12", "6.3x3.2");

        testPackage(converter, "1206 (3216 Metric)", "1206", "3216", "12x6", "3.2x1.6");
    }

    private void testPackage(PcbPartsUtils.PackageConvert converter, String input,
                             String expectedImperialSize, String expectedMetricSize,
                             String expectedImperial, String expectedMetric) {
        Map<Unit, String> result = converter.convert(input);
        assertEquals(expectedImperialSize, result.get(Unit.IMPERIAL_SIZE));
        assertEquals(expectedMetricSize, result.get(Unit.METRIC_SIZE));
        assertEquals(expectedImperial, result.get(Unit.IMPERIAL));
        assertEquals(expectedMetric, result.get(Unit.METRIC));
    }
}
