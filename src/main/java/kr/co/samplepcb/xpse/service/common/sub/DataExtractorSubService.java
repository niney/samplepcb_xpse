package kr.co.samplepcb.xpse.service.common.sub;

import coolib.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * DataExtractorSubService 클래스는 특정 데이터 파일로부터 사이즈 정보를 추출하여
 * 이를 숫자 형식의 사이즈와 다른 형식의 사이즈로 분류하는 기능을 제공한다.
 *
 * 이 클래스는 초기화 블록을 통해 "partsPackageOnly.json" 파일을 로드하고,
 * 해당 파일의 내용을 읽어 사이즈 리스트를 초기화한다.
 * 또한, extractSizeFromTitle 메서드를 통해 제목에서 사이즈 정보를 추출할 수 있다.
 */
@Service
public class DataExtractorSubService {
    private static final Logger log = LoggerFactory.getLogger(DataExtractorSubService.class);

    static List<String> sizes;
    List<String> numericSizes;
    List<String> otherSizes;

    static {
        try {
            InputStream is = DataExtractorSubService.class.getClassLoader().getResourceAsStream("partsPackageOnly.json");
            if (is == null) throw new FileNotFoundException("File not found!");
            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8);
            String content = scanner.useDelimiter("\\A").next();

            String[] sizeArray = CommonUtils.getObjectMapper().readValue(content, String[].class);
            sizes = new ArrayList<>();
            for (String size : sizeArray) {
                if (size.length() > 1) {
                    sizes.add(size);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * DataExtractorSubService 클래스의 기본 생성자입니다.
     *
     * 이 생성자는 인스턴스가 생성될 때 splitSizes() 메서드를 호출하여
     * 사이즈 정보를 숫자 형식의 사이즈와 다른 형식의 사이즈로 분류합니다.
     */
    public DataExtractorSubService() {
        splitSizes();
    }

    /**
     * sizes 리스트를 숫자 형식의 사이즈와 다른 형식의 사이즈로 분류합니다.
     *
     * 숫자 형식의 사이즈는 정규 표현식을 이용하여 검출합니다.
     * 모든 숫자 형식의 사이즈는 numericSizes 리스트에 저장되고,
     * 나머지 사이즈는 otherSizes 리스트에 저장됩니다.
     */
    private void splitSizes() {
        Pattern numPattern = Pattern.compile("\\d+(\\.\\d+)?");
        numericSizes = new ArrayList<>();
        otherSizes = new ArrayList<>();
        for (String size : sizes) {
            if (numPattern.matcher(size).matches()) {
                numericSizes.add(size);
            } else {
                otherSizes.add(size);
            }
        }
    }

    /**
     * 제목에서 사이즈 정보를 추출합니다.
     * 주어진 제목이 문자열이거나 문자열의 리스트여야 합니다.
     *
     * @param titles 사이즈 정보를 추출할 제목, 문자열 또는 문자열 리스트.
     * @return 추출된 사이즈 정보를 문자열로 반환합니다. 제목에서 사이즈 정보를 찾을 수 없는 경우 null을 반환합니다.
     * @throws IllegalArgumentException titles 파라미터가 문자열 또는 문자열 리스트가 아닌 경우 발생합니다.
     */
    public String extractSizeFromTitle(Object titles) {
        List<String> titleWords = new ArrayList<>();
        if (titles instanceof List<?>) {
            for (Object title : (List<?>) titles) {
                titleWords.add(((String) title).toLowerCase());
            }
        } else if (titles instanceof String) {
            titleWords = Arrays.asList(((String) titles).toLowerCase().split(" "));
        } else {
            throw new IllegalArgumentException("titles should be either a string or a list");
        }

        for (String size : numericSizes) {
            for (String word : titleWords) {
                if (word.contains(size)) {
                    return size;
                }
            }
        }

        for (String size : otherSizes) {
            if (titleWords.contains(size.toLowerCase())) {
                return size;
            }
        }

        return null;
    }
}
