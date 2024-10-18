package kr.co.samplepcb.xpse.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;

public class CoolStringUtils {

    /**
     * 주어진 문자열에서 숫자를 추출하고 반올림하여 반환합니다.
     *
     * @param value 숫자를 포함할 수 있는 문자열
     * @return 추출된 숫자를 반올림한 값, 숫자가 없거나 잘못된 형식인 경우 0
     */
    public static int extractAndRoundNumber(String value) {
        // 숫자와 소수점을 제외한 모든 문자 제거
        String numericString = value.replaceAll("[^\\d.]", "");
        if (StringUtils.isEmpty(numericString)) {
            return 0;
        }
        try {
            // 문자열을 double 형으로 변환하고 반올림 수행
            return (int) Math.round(Double.parseDouble(numericString));
        } catch (NumberFormatException e) {
            // 숫자로 변환할 수 없는 경우 처리
            return 0; // 또는 다른 적절한 기본값이나 예외 처리
        }
    }
}
