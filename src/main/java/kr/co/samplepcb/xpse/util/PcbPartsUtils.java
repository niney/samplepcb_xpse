package kr.co.samplepcb.xpse.util;

import kr.co.samplepcb.xpse.domain.PcbUnitSearch;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PcbPartsUtils {

    /**
     * 미리 정의된 패턴에 기반하여 문자열을 파싱하고 각 구성요소를 분류합니다
     *
     * @param text the input string to be parsed
     * @return a Map containing various classifications of the input string
     */
    public static Map<String, List<String>> parseString(String text) {
        Map<String, List<String>> classifications = new HashMap<>();
        classifications.put(PcbPartsSearchField.PRODUCT_NAME, new ArrayList<>());

        Map<String, Pattern> units = new HashMap<>();
        units.put(PcbPartsSearchField.WATT, Pattern.compile("([0-9.]+/[0-9.]+|[0-9.]+)\\s*([Ww]|watt(s)?|WATT(S)?)\\b", Pattern.CASE_INSENSITIVE));
        units.put(PcbPartsSearchField.TOLERANCE, Pattern.compile("[±]?[0-9.]+(\\s*%)"));
        units.put(PcbPartsSearchField.OHM, Pattern.compile("([0-9.]+/[0-9.]+|[0-9.]+)\\s*(k|m|G)(?:(ohm(s)?|Ω)\\b|\\b)", Pattern.CASE_INSENSITIVE));
        units.put(PcbPartsSearchField.CONDENSER, Pattern.compile("[0-9.]+(?:μF|µF|uF|nF|pF|mF|F)(?![a-zA-Z])", Pattern.CASE_INSENSITIVE));
        units.put(PcbPartsSearchField.VOLTAGE, Pattern.compile("([0-9.]+/[0-9.]+)?[0-9.]*\\s*(V|v|kV|KV|kv|mV|MV|mv|µV|UV|uv|Volt|volt|vdc|VDC|kvdc|KVDC)\\b", Pattern.CASE_INSENSITIVE));
        units.put(PcbPartsSearchField.TEMPERATURE, Pattern.compile("(-?\\d+\\.?\\d*)\\s?(℃|°C)"));
        units.put(PcbPartsSearchField.SIZE, Pattern.compile("((\\d+\\.\\d+|\\d+)([xX*])(\\d+\\.\\d+|\\d+)(([xX*])(\\d+\\.\\d+|\\d+))?)|((\\d+)(?=사이즈))|(\\d+\\.?\\d*mm)", Pattern.CASE_INSENSITIVE));
        units.put(PcbPartsSearchField.INDUCTOR, Pattern.compile("[0-9.]+(?:pH|nH|uH|mH|H)(?![a-zA-Z])", Pattern.CASE_INSENSITIVE));
        units.put(PcbPartsSearchField.CURRENT, Pattern.compile("[0-9.]+(?:uA|µA|mA|A)(?![a-zA-Z])", Pattern.CASE_INSENSITIVE));

        String[] tokens = text.split("\\s+");
        for (String token : tokens) {
            boolean matched = false;
            for (Map.Entry<String, Pattern> unit : units.entrySet()) {
                Matcher matcher = unit.getValue().matcher(token);
                if (matcher.find()) {
                    matched = true;
                    List<String> classification = classifications.getOrDefault(unit.getKey(), new ArrayList<>());
                    if (unit.getKey().equals(PcbPartsSearchField.TEMPERATURE)) {
                        classification.add(matcher.group(1));
                    } else {
                        classification.add(matcher.group());
                    }
                    classifications.put(unit.getKey(), classification);
                }
            }

            if (!matched) {
                classifications.get(PcbPartsSearchField.PRODUCT_NAME).add(token);
            }
        }

        return classifications;
    }

    private static String stringifyDoubleWithConditionalDecimal(double number) {
        String str;
        if (number == Math.floor(number)) {
            // 정수값이면 소수점 제거
            str = String.format("%.0f", number);
        } else {
            // 소수점이 있는 경우 그대로 문자열로 변환
            str = String.valueOf(number);
        }
        return str;
    }

    private static String stringifyStringWithConditionalDecimal(String input) {
        double number;
        try {
            number = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return "Invalid input"; // 입력이 유효한 숫자가 아닐 경우
        }

        return stringifyDoubleWithConditionalDecimal(number);
    }

    public static abstract class PcbConvert {
        public enum Unit {
            NONE,
            WATTS, KILOWATTS, MEGAWATTS, MILLIWATTS, MICROWATTS,
            FARADS, MEGAFARADS, KILOFARADS, MILLIFARADS, MICROFARADS, NANOFARADS, PICOFARADS,
            PERCENT, PERCENT_STRING,
            VOLTS, KILOVOLTS, MILLIVOLTS, MICROVOLTS,
            AMPERES, MILLIAMPERES, MICROAMPERES,
            OHMS, KILOHMS, MEGAOHMS,
            HENRYS, MILLIHENRYS, MICROHENRYS,
            IMPERIAL, METRIC, IMPERIAL_SIZE, METRIC_SIZE,
        }

        abstract Map<Unit, String> convert(String input);

        abstract Unit determineUnit(String unitStr);

        abstract double convert(double value, Unit unit);

    }

    public static class WattConvert extends PcbConvert {

        @Override
        public Map<Unit, String> convert(String input) {
            String[] parts = input.split("(?<=\\d)(?=\\D)");
            if (parts.length < 2) {
                return Collections.emptyMap();
            }

            if (parts[0].contains("/")) {
                String[] rangeParts = parts[0].split("/");
                parts[0] = rangeParts[0];
            }

            double value = Double.parseDouble(parts[0]);
            Unit unit = determineUnit(parts[1]);
            if (unit == Unit.NONE) {
                return Collections.emptyMap();
            }

            // Convert to Watts
            double valueInWatts = convert(value, unit);

            // Convert to other units
            double valueInKilowatts = convertWattsToKilowatts(valueInWatts);
            double valueInMegawatts = convertWattsToMegawatts(valueInWatts);
            double valueInMilliwatts = convertWattsToMilliwatts(valueInWatts);
            double valueInMicrowatts = convertWattsToMicrowatts(valueInWatts);

            Map<Unit, String> result = new HashMap<>();
            result.put(Unit.WATTS, stringifyDoubleWithConditionalDecimal(valueInWatts) + "W");
            result.put(Unit.KILOWATTS, stringifyDoubleWithConditionalDecimal(valueInKilowatts) + "kW");
            result.put(Unit.MEGAWATTS, stringifyDoubleWithConditionalDecimal(valueInMegawatts) + "MW");
            result.put(Unit.MILLIWATTS, stringifyDoubleWithConditionalDecimal(valueInMilliwatts) + "mW");
            result.put(Unit.MICROWATTS, stringifyDoubleWithConditionalDecimal(valueInMicrowatts) + "uW");
            return result;
        }

        @Override
        Unit determineUnit(String unitStr) {
            unitStr = unitStr.toLowerCase();
            switch (unitStr) {
                case "w":
                case "watt":
                case "watts":
                    return Unit.WATTS;
                case "kw":
                    return Unit.KILOWATTS;
                case "mw":
                case "megaw":
                    return Unit.MEGAWATTS;
                case "mlw":
                case "milliw":
                    return Unit.MILLIWATTS;
                case "µw":
                case "uw":
                    return Unit.MICROWATTS;
                default:
                    return Unit.NONE;
            }
        }

        @Override
        double convert(double value, Unit unit) {
            switch (unit) {
                case WATTS:
                    return value;
                case KILOWATTS:
                    return value * 1_000;
                case MEGAWATTS:
                    return value * 1_000_000;
                case MILLIWATTS:
                    return value / 1_000;
                case MICROWATTS:
                    return value / 1_000_000;
                default:
                    throw new IllegalArgumentException("Unknown unit: " + unit);
            }
        }

        private static double convertWattsToKilowatts(double watts) {
            return watts / 1_000;
        }

        private static double convertWattsToMegawatts(double watts) {
            return watts / 1_000_000;
        }

        private static double convertWattsToMilliwatts(double watts) {
            return watts * 1_000;
        }

        private static double convertWattsToMicrowatts(double watts) {
            return watts * 1_000_000;
        }
    }


    public static class OhmConvert extends PcbConvert {

        @Override
        public Map<Unit, String> convert(String input) {
            String[] parts = input.split("(?<=\\d)(?=\\D)");
            if (parts.length < 2) {
                return Collections.emptyMap();
            }
            double value = Double.parseDouble(parts[0]);
            Unit unit = determineUnit(parts[1]);
            if (unit == Unit.NONE) {
                return Collections.emptyMap();
            }
            // Convert to Ohms
            double valueInOhms = convert(value, unit);

            // Convert to other units
            double valueInKilohms = convertOhmsToKilohms(valueInOhms);
            double valueInMegaohms = convertOhmsToMegaohms(valueInOhms);

            Map<Unit, String> result = new HashMap<>();
            result.put(Unit.OHMS, stringifyDoubleWithConditionalDecimal(valueInOhms) + "ohm");
            result.put(Unit.KILOHMS, stringifyDoubleWithConditionalDecimal(valueInKilohms) + "kohm");
            result.put(Unit.MEGAOHMS, stringifyDoubleWithConditionalDecimal(valueInMegaohms) + "mohm");

            return result;
        }

        @Override
        Unit determineUnit(String unitStr) {
            unitStr = unitStr.toLowerCase();
            switch (unitStr) {
                case "Ω":
                case "ohm":
                case "ohms":
                    return Unit.OHMS;
                case "kΩ":
                case "kohm":
                case "kohms":
                    return Unit.KILOHMS;
                case "mΩ":
                case "mohm":
                case "mohms":
                    return Unit.MEGAOHMS;
                default:
                    return Unit.NONE;
            }
        }

        @Override
        double convert(double value, Unit unit) {
            switch (unit) {
                case OHMS:
                    return value; // Ω to Ω
                case KILOHMS:
                    return value * 1_000; // kΩ to Ω
                case MEGAOHMS:
                    return value * 1_000_000; // MΩ to Ω
                default:
                    throw new IllegalArgumentException("Unknown unit: " + unit);
            }
        }

        private static double convertOhmsToKilohms(double ohms) {
            return ohms / 1_000; // Ω to kΩ
        }

        private static double convertOhmsToMegaohms(double ohms) {
            return ohms / 1_000_000; // Ω to MΩ
        }
    }

    public static class FaradsConvert extends PcbConvert {

        @Override
        public Map<Unit, String> convert(String input) {
            String[] parts = input.split("(?<=\\d)(?=\\D)");
            if (parts.length < 2) {
                return Collections.emptyMap();
            }
            double value = Double.parseDouble(parts[0]);
            Unit unit = determineUnit(parts[1]);
            if (unit == Unit.NONE) {
                return Collections.emptyMap();
            }
            // Farads로 변환
            double valueInFarads = convert(value, unit);

            // 다른 단위로 변환
            double valueInMegafarads = convertFaradsToMegafarads(valueInFarads);
            double valueInKilofarads = convertFaradsToKilofarads(valueInFarads);
            double valueInMillifarads = convertFaradsToMillifarads(valueInFarads);
            double valueInMicrofarads = convertFaradsToMicrofarads(valueInFarads);
            double valueInNanofarads = convertFaradsToNanofarads(valueInFarads);
            double valueInPicofarads = convertFaradsToPicofarads(valueInFarads);

            Map<Unit, String> result = new HashMap<>();
            result.put(Unit.FARADS, stringifyDoubleWithConditionalDecimal(valueInFarads) + "F");
            result.put(Unit.MEGAFARADS, stringifyDoubleWithConditionalDecimal(valueInMegafarads) + "MF");
            result.put(Unit.KILOFARADS, stringifyDoubleWithConditionalDecimal(valueInKilofarads) + "kF");
            result.put(Unit.MILLIFARADS, stringifyDoubleWithConditionalDecimal(valueInMillifarads) + "mF");
            result.put(Unit.MICROFARADS, stringifyDoubleWithConditionalDecimal(valueInMicrofarads) + "uF");
            result.put(Unit.NANOFARADS, stringifyDoubleWithConditionalDecimal(valueInNanofarads) + "nF");
            result.put(Unit.PICOFARADS, stringifyDoubleWithConditionalDecimal(valueInPicofarads) + "pF");
            return result;
        }

        @Override
        public Unit determineUnit(String unitStr) {
            unitStr = unitStr.replace("f", "F");
            switch (unitStr) {
                case "MF":
                    return Unit.MEGAFARADS;
                case "kF":
                    return Unit.KILOFARADS;
                case "mF":
                    return Unit.MILLIFARADS;
                case "uF":
                    return Unit.MICROFARADS;
                case "nF":
                    return Unit.NANOFARADS;
                case "pF":
                    return Unit.PICOFARADS;
                default:
                    return Unit.NONE;
            }
        }

        @Override
        public double convert(double value, Unit unit) {
            switch (unit) {
                case MEGAFARADS:
                    return value * 1_000_000; // MF to F
                case KILOFARADS:
                    return value * 1_000; // kF to F
                case MILLIFARADS:
                    return value / 1_000; // mF to F
                case MICROFARADS:
                    return value / 1_000_000; // μF to F
                case NANOFARADS:
                    return value / 1_000_000_000; // nF to F
                case PICOFARADS:
                    return value / 1_000_000_000_000L; // pF to F
                default:
                    throw new IllegalArgumentException("Unknown unit: " + unit);
            }
        }

        private double convertFaradsToMegafarads(double farads) {
            return farads / 1_000_000; // F to MF
        }

        private double convertFaradsToKilofarads(double farads) {
            return farads / 1_000; // F to kF
        }

        private double convertFaradsToMillifarads(double farads) {
            return farads * 1_000; // F to mF
        }

        private double convertFaradsToMicrofarads(double farads) {
            return farads * 1_000_000; // F to μF
        }

        private double convertFaradsToNanofarads(double farads) {
            return farads * 1_000_000_000; // F to nF
        }

        private double convertFaradsToPicofarads(double farads) {
            return farads * 1_000_000_000_000L; // F to pF
        }
    }

    public static class ToleranceConvert extends PcbConvert {

        @Override
        public Map<Unit, String> convert(String input) {
            Map<Unit, String> result = new HashMap<>();
            String[] parts = input.split("(?<=\\d)(?=\\D)");
            if (StringUtils.isEmpty(parts[0])) {
                return Collections.emptyMap();
            }
            result.put(Unit.PERCENT, stringifyStringWithConditionalDecimal(parts[0]) + "%");
            result.put(Unit.PERCENT_STRING, stringifyStringWithConditionalDecimal(parts[0]) + "percent");

            return result;
        }

        @Override
        Unit determineUnit(String unitStr) {
            return Unit.NONE;
        }

        @Override
        double convert(double value, Unit unit) {
            return 0;
        }
    }

    public static class VoltConvert extends PcbConvert {

        @Override
        public Map<Unit, String> convert(String input) {
            String[] parts = input.split("(?<=\\d)(?=\\D)");
            if (parts.length < 2) {
                return Collections.emptyMap();
            }
            double value = Double.parseDouble(parts[0]);
            Unit unit = determineUnit(parts[1]);
            if (unit == Unit.NONE) {
                return Collections.emptyMap();
            }

            // Volts로 변환
            double valueInVolts = convert(value, unit);

            // 다른 단위로 변환
            double valueInKilovolts = convertVoltsToKilovolts(valueInVolts);
            double valueInMillivolts = convertVoltsToMillivolts(valueInVolts);
            double valueInMicrovolts = convertVoltsToMicrovolts(valueInVolts);

            Map<Unit, String> result = new HashMap<>();
            result.put(Unit.VOLTS, stringifyDoubleWithConditionalDecimal(valueInVolts) + "V");
            result.put(Unit.KILOVOLTS, stringifyDoubleWithConditionalDecimal(valueInKilovolts) + "kV");
            result.put(Unit.MILLIVOLTS, stringifyDoubleWithConditionalDecimal(valueInMillivolts) + "mV");
            result.put(Unit.MICROVOLTS, stringifyDoubleWithConditionalDecimal(valueInMicrovolts) + "uV");
            return result;
        }

        @Override
        Unit determineUnit(String unitStr) {
            unitStr = unitStr.replace("v", "V");
            switch (unitStr) {
                case "kV":
                    return Unit.KILOVOLTS;
                case "V":
                    return Unit.VOLTS;
                case "mV":
                    return Unit.MILLIVOLTS;
                case "uV":
                    return Unit.MICROVOLTS;
                default:
                    return Unit.NONE;
            }
        }

        @Override
        double convert(double value, Unit unit) {
            switch (unit) {
                case KILOVOLTS:
                    return value * 1_000; // kV to V
                case VOLTS:
                    return value; // V to V
                case MILLIVOLTS:
                    return value / 1_000; // mV to V
                case MICROVOLTS:
                    return value / 1_000_000; // µV to V
                default:
                    throw new IllegalArgumentException("Unknown unit: " + unit);
            }
        }

        private static double convertVoltsToKilovolts(double volts) {
            return volts / 1_000; // V to kV
        }

        private static double convertVoltsToMillivolts(double volts) {
            return volts * 1_000; // V to mV
        }

        private static double convertVoltsToMicrovolts(double volts) {
            return volts * 1_000_000; // V to µV
        }
    }

    public static class CurrentConvert extends PcbConvert {

        @Override
        public Map<Unit, String> convert(String input) {
            String[] parts = input.split("(?<=\\d)(?=\\D)");
            if (parts.length < 2) {
                return Collections.emptyMap();
            }
            double value = Double.parseDouble(parts[0]);
            Unit unit = determineUnit(parts[1]);
            if (unit == Unit.NONE) {
                return Collections.emptyMap();
            }

            // Amperes로 변환
            double valueInAmperes = convert(value, unit);

            // 다른 단위로 변환
            double valueInMilliamperes = convertAmperesToMilliamperes(valueInAmperes);
            double valueInMicroamperes = convertAmperesToMicroamperes(valueInAmperes);

            // 결과 출력
            Map<Unit, String> result = new HashMap<>();
            result.put(Unit.AMPERES, stringifyDoubleWithConditionalDecimal(valueInAmperes) + "A");
            result.put(Unit.MILLIAMPERES, stringifyDoubleWithConditionalDecimal(valueInMilliamperes) + "mA");
            result.put(Unit.MICROAMPERES, stringifyDoubleWithConditionalDecimal(valueInMicroamperes) + "uA");
            return result;

        }

        @Override
        Unit determineUnit(String unitStr) {
            unitStr = unitStr.replace("a", "A");
            switch (unitStr) {
                case "A":
                    return Unit.AMPERES;
                case "mA":
                    return Unit.MILLIAMPERES;
                case "uA":
                    return Unit.MICROAMPERES;
                default:
                    return Unit.NONE;
            }
        }

        @Override
        double convert(double value, Unit unit) {
            switch (unit) {
                case AMPERES:
                    return value; // A to A
                case MILLIAMPERES:
                    return value / 1_000; // mA to A
                case MICROAMPERES:
                    return value / 1_000_000; // µA to A
                default:
                    throw new IllegalArgumentException("Unknown unit: " + unit);
            }
        }

        private static double convertAmperesToMilliamperes(double amperes) {
            return amperes * 1_000; // A to mA
        }

        private static double convertAmperesToMicroamperes(double amperes) {
            return amperes * 1_000_000; // A to µA
        }
    }

    public static class InductorConvert extends PcbConvert {

        @Override
        public Map<Unit, String> convert(String input) {
            String[] parts = input.split("(?<=\\d)(?=\\D)");
            if (parts.length < 2) {
                return Collections.emptyMap();
            }
            double value = Double.parseDouble(parts[0]);
            Unit unit = determineUnit(parts[1]);
            if (unit == Unit.NONE) {
                return Collections.emptyMap();
            }
            // Convert to Henrys
            double valueInHenrys = convert(value, unit);

            // Convert to other units
            double valueInMillihenrys = convertHenrysToMillihenrys(valueInHenrys);
            double valueInMicrohenrys = convertHenrysToMicrohenrys(valueInHenrys);

            // 결과 출력
            Map<Unit, String> result = new HashMap<>();
            result.put(Unit.HENRYS, stringifyDoubleWithConditionalDecimal(valueInHenrys) + "H");
            result.put(Unit.MILLIHENRYS, stringifyDoubleWithConditionalDecimal(valueInMillihenrys) + "mH");
            result.put(Unit.MICROHENRYS, stringifyDoubleWithConditionalDecimal(valueInMicrohenrys) + "uH");
            return result;
        }

        @Override
        Unit determineUnit(String unitStr) {
            switch (unitStr) {
                case "H":
                    return Unit.HENRYS;
                case "mH":
                    return Unit.MILLIHENRYS;
                case "µH":
                    return Unit.MICROHENRYS;
                default:
                    return Unit.NONE;
            }
        }

        @Override
        double convert(double value, Unit unit) {
            switch (unit) {
                case HENRYS:
                    return value; // H to H
                case MILLIHENRYS:
                    return value / 1_000; // mH to H
                case MICROHENRYS:
                    return value / 1_000_000; // µH to H
                default:
                    throw new IllegalArgumentException("Unknown unit: " + unit);
            }
        }

        private static double convertHenrysToMillihenrys(double henrys) {
            return henrys * 1_000; // H to mH
        }

        private static double convertHenrysToMicrohenrys(double henrys) {
            return henrys * 1_000_000; // H to µH
        }
    }

    public static class PackageConvert extends PcbConvert {
        private static final double INCH_TO_MM = 25.4;

        private static final Map<String, String> IMPERIAL_TO_METRIC = Map.of(
                "0402", "1005",
                "0603", "1608",
                "0805", "2012",
                "1206", "3216",
                "1210", "3225",
                "2010", "5025",
                "2512", "6332"
        );

        @Override
        public Map<Unit, String> convert(String input) {
            if (input == null || input.trim().isEmpty()) {
                return Collections.emptyMap();
            }

            Map<Unit, String> result = new HashMap<>();

            // Case 1: 1206(3216) format
            Pattern fullPattern = Pattern.compile("^\\s*(\\d{4})\\s*\\(\\s*(\\d{4})\\s*\\)\\s*$");
            // Case 2: 1206 (3216 Metric) format
            Pattern metricPattern = Pattern.compile("^\\s*(\\d{4})\\s*\\(\\s*(\\d{4})\\s*(?:Metric)?\\s*\\)\\s*$", Pattern.CASE_INSENSITIVE);
            // Case 3: 1206 format
            Pattern singlePattern = Pattern.compile("^\\s*(\\d{4})\\s*$");

            Matcher fullMatcher = fullPattern.matcher(input.trim());
            Matcher metricMatcher = metricPattern.matcher(input.trim());
            Matcher singleMatcher = singlePattern.matcher(input.trim());

            if (fullMatcher.find()) {
                String firstCode = fullMatcher.group(1);
                String secondCode = fullMatcher.group(2);
                processPackageCodes(result, firstCode, secondCode);
            } else if (metricMatcher.find()) {
                String firstCode = metricMatcher.group(1);
                String secondCode = metricMatcher.group(2);
                processPackageCodes(result, firstCode, secondCode);
            } else if (singleMatcher.find()) {
                String imperialCode = singleMatcher.group(1);
                String metricCode = IMPERIAL_TO_METRIC.get(imperialCode);
                if (metricCode != null) {
                    processPackageCodes(result, imperialCode, metricCode);
                }
            }

            return result;
        }

        private void processPackageCodes(Map<Unit, String> result, String firstCode, String secondCode) {
            String imperial, metric;
            if (IMPERIAL_TO_METRIC.containsValue(firstCode)) {
                metric = firstCode;
                imperial = secondCode;
            } else {
                imperial = firstCode;
                metric = secondCode;
            }

            result.put(Unit.IMPERIAL_SIZE, imperial);
            result.put(Unit.METRIC_SIZE, metric);

            int imperialLength = Integer.parseInt(imperial.substring(0, 2));
            int imperialWidth = Integer.parseInt(imperial.substring(2));

            double lengthInMm = Double.parseDouble(metric.substring(0, 2)) / 10.0;
            double widthInMm = Double.parseDouble(metric.substring(2)) / 10.0;

            result.put(Unit.IMPERIAL, String.format("%dx%d", imperialLength, imperialWidth));
            result.put(Unit.METRIC, String.format("%.1fx%.1f", lengthInMm, widthInMm));
        }

        @Override
        Unit determineUnit(String unitStr) {
            return Unit.NONE;
        }

        @Override
        double convert(double value, Unit unit) {
            return 0;
        }
    }

    // 레벤슈타인 거리 계산
    public static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[a.length()][b.length()];
    }

    // 유사도 점수 (백분율)로 변환
    public static double similarityScorePercentage(String a, String b) {
        int maxLength = Math.max(a.length(), b.length());
        if (maxLength == 0) return 100.0; // 두 문자열 모두 비어있는 경우
        return (1.0 - (double) levenshteinDistance(a, b) / maxLength) * 100;
    }

    public static PcbUnitSearch parsingToPcbUnitSearch(String propertyName, String value) {
        PcbUnitSearch pcbUnitSearch = new PcbUnitSearch();
        List<String> parsedSearchResults = PcbPartsUtils.parseString(value).get(propertyName);
        if (CollectionUtils.isNotEmpty(parsedSearchResults)) {
            String searchValue = parsedSearchResults.getFirst();
            String lowerCaseString = searchValue.toLowerCase();
            // μF와 µF를 uF로 대체
            String replacedString = lowerCaseString
                    .replace("μF", "uF")
                    .replace("µF", "uF")
                    .replace("uf", "uF")
                    .replace("μV", "uV")
                    .replace("µV", "uV")
                    .replace("uv", "uV")
                    .replace("μA", "uA")
                    .replace("µA", "uA")
                    .replace("ua", "uA")
                    .replace("Ω", "Ohm")
                    .replace("±", "");
            if (propertyName.equals(PcbPartsSearchField.WATT)) {
                PcbPartsUtils.WattConvert wattConvert = new PcbPartsUtils.WattConvert();
                Map<PcbPartsUtils.PcbConvert.Unit, String> watt = wattConvert.convert(replacedString);
                pcbUnitSearch.setField1(watt.get(PcbPartsUtils.PcbConvert.Unit.WATTS));
                pcbUnitSearch.setField2(watt.get(PcbPartsUtils.PcbConvert.Unit.KILOWATTS));
                pcbUnitSearch.setField3(watt.get(PcbPartsUtils.PcbConvert.Unit.MEGAWATTS));
                pcbUnitSearch.setField4(watt.get(PcbPartsUtils.PcbConvert.Unit.MILLIWATTS));
                pcbUnitSearch.setField5(watt.get(PcbPartsUtils.PcbConvert.Unit.MICROWATTS));
            }

            if (propertyName.equals(PcbPartsSearchField.CONDENSER)) {
                // 소문자로 변환
                PcbPartsUtils.FaradsConvert faradsConvert = new PcbPartsUtils.FaradsConvert();
                Map<PcbPartsUtils.FaradsConvert.Unit, String> faradsMap = faradsConvert.convert(replacedString);
                pcbUnitSearch.setField1(faradsMap.get(PcbPartsUtils.PcbConvert.Unit.FARADS));
                pcbUnitSearch.setField2(faradsMap.get(PcbPartsUtils.PcbConvert.Unit.MICROFARADS));
                pcbUnitSearch.setField3(faradsMap.get(PcbPartsUtils.PcbConvert.Unit.NANOFARADS));
                pcbUnitSearch.setField4(faradsMap.get(PcbPartsUtils.PcbConvert.Unit.PICOFARADS));
            }

            if (propertyName.equals(PcbPartsSearchField.TOLERANCE)) {
                PcbPartsUtils.ToleranceConvert toleranceConvert = new PcbPartsUtils.ToleranceConvert();
                Map<PcbPartsUtils.PcbConvert.Unit, String> tolerance = toleranceConvert.convert(replacedString);
                pcbUnitSearch.setField1(tolerance.get(PcbPartsUtils.PcbConvert.Unit.PERCENT));
                pcbUnitSearch.setField2(tolerance.get(PcbPartsUtils.PcbConvert.Unit.PERCENT_STRING));
            }

            if (propertyName.equals(PcbPartsSearchField.OHM)) {
                PcbPartsUtils.OhmConvert ohmConvert = new PcbPartsUtils.OhmConvert();
                Map<PcbPartsUtils.PcbConvert.Unit, String> tolerance = ohmConvert.convert(replacedString);
                pcbUnitSearch.setField1(tolerance.get(PcbPartsUtils.PcbConvert.Unit.OHMS));
                pcbUnitSearch.setField2(tolerance.get(PcbPartsUtils.PcbConvert.Unit.KILOHMS));
                pcbUnitSearch.setField3(tolerance.get(PcbPartsUtils.PcbConvert.Unit.MEGAOHMS));
            }

            if (propertyName.equals(PcbPartsSearchField.VOLTAGE)) {
                PcbPartsUtils.VoltConvert voltageConvert = new PcbPartsUtils.VoltConvert();
                Map<PcbPartsUtils.PcbConvert.Unit, String> voltage = voltageConvert.convert(replacedString);
                pcbUnitSearch.setField1(voltage.get(PcbPartsUtils.PcbConvert.Unit.VOLTS));
                pcbUnitSearch.setField2(voltage.get(PcbPartsUtils.PcbConvert.Unit.KILOVOLTS));
                pcbUnitSearch.setField3(voltage.get(PcbPartsUtils.PcbConvert.Unit.MILLIVOLTS));
                pcbUnitSearch.setField4(voltage.get(PcbPartsUtils.PcbConvert.Unit.MICROVOLTS));
            }

            if (propertyName.equals(PcbPartsSearchField.CURRENT)) {
                PcbPartsUtils.CurrentConvert currentConvert = new PcbPartsUtils.CurrentConvert();
                Map<PcbPartsUtils.PcbConvert.Unit, String> current = currentConvert.convert(replacedString);
                pcbUnitSearch.setField1(current.get(PcbPartsUtils.PcbConvert.Unit.AMPERES));
                pcbUnitSearch.setField2(current.get(PcbPartsUtils.PcbConvert.Unit.MILLIAMPERES));
                pcbUnitSearch.setField3(current.get(PcbPartsUtils.PcbConvert.Unit.MICROAMPERES));
            }

            if (propertyName.equals(PcbPartsSearchField.INDUCTOR)) {
                PcbPartsUtils.InductorConvert inductorConvert = new PcbPartsUtils.InductorConvert();
                Map<PcbPartsUtils.PcbConvert.Unit, String> inductor = inductorConvert.convert(replacedString);
                pcbUnitSearch.setField1(inductor.get(PcbPartsUtils.PcbConvert.Unit.HENRYS));
                pcbUnitSearch.setField2(inductor.get(PcbPartsUtils.PcbConvert.Unit.MILLIHENRYS));
                pcbUnitSearch.setField3(inductor.get(PcbPartsUtils.PcbConvert.Unit.MICROHENRYS));
            }
        }
        if (propertyName.equals(PcbPartsSearchField.PACKAGING)) {
            PcbPartsUtils.PackageConvert packageConvert = new PcbPartsUtils.PackageConvert();
            Map<PcbPartsUtils.PcbConvert.Unit, String> packageMap = packageConvert.convert(value);
            pcbUnitSearch.setField1(packageMap.get(PcbPartsUtils.PcbConvert.Unit.IMPERIAL_SIZE));
            pcbUnitSearch.setField2(packageMap.get(PcbPartsUtils.PcbConvert.Unit.METRIC_SIZE));
            pcbUnitSearch.setField3(packageMap.get(PcbPartsUtils.PcbConvert.Unit.IMPERIAL));
            pcbUnitSearch.setField4(packageMap.get(PcbPartsUtils.PcbConvert.Unit.METRIC));
        }

        return pcbUnitSearch;
    }

}
