package kr.co.samplepcb.xpse.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DigikeyUtils {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\b[A-Za-z]{3,}\\b");

    public static Set<String> extractWords(Map<String, Object> data) {
        Set<String> words = new HashSet<>();
        extractWordsFromCategories(data, words);
        return words;
    }

    @SuppressWarnings("unchecked")
    private static void extractWordsFromCategories(Map<String, Object> data, Set<String> words) {
        List<Map<String, Object>> categories = (List<Map<String, Object>>) data.get("Categories");

        for (Map<String, Object> category : categories) {
            // 현재 카테고리의 Name 필드에서 단어 추출
            String name = (String) category.get("Name");
            if (name != null) {
                Matcher matcher = WORD_PATTERN.matcher(name);
                while (matcher.find()) {
                    words.add(matcher.group().toLowerCase());
                }
            }

            // 하위 카테고리 처리
            List<Map<String, Object>> children = (List<Map<String, Object>>) category.get("Children");
            if (children != null && !children.isEmpty()) {
                for (Map<String, Object> child : children) {
                    Map<String, Object> childData = new HashMap<>();
                    childData.put("Categories", Collections.singletonList(child));
                    extractWordsFromCategories(childData, words);
                }
            }
        }
    }
}
