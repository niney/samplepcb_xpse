package kr.co.samplepcb.xpse.service.common.sub;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.PcbPartsPriceSearch;
import kr.co.samplepcb.xpse.domain.PcbPartsPriceStepSearch;
import kr.co.samplepcb.xpse.domain.PcbPartsSearch;
import kr.co.samplepcb.xpse.domain.PcbUnitSearch;
import kr.co.samplepcb.xpse.pojo.PcbPartsSearchField;
import kr.co.samplepcb.xpse.util.PcbPartsUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DigikeyPartsParserSubService 클래스는 Digi-Key의 제품 정보를 파싱하여 PcbPartsSearch 객체로 변환하는 기능을 제공합니다.
 *
 * 이 클래스는 제품 정보의 기본 정보, 카테고리 정보, 가격 정보 및 파라미터 정보를 설정하는 다양한 메서드를 포함하고 있습니다.
 * 또한 특정 파라미터 ID에 따라 적절한 필드로 매핑하고, 이 과정에서 필요에 따라 공백을 처리하는 로직을 가지고 있습니다.
 *
 * 이 서비스는 기본적으로 Map 형식의 데이터를 입력받아 처리하며, 결과적으로 CCObjectResult를 반환합니다.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class DigikeyPartsParserSubService {
    private static final class ParameterIds {
        static final int WATT = 2;
        static final int TOLERANCE = 3;
        static final int OHM = 2085;
        static final int CONDENSER = 2049;
        static final int VOLTAGE = 14;
        static final int CURRENT = 714;
        static final int INDUCTOR = 2087;
        static final int PACKAGING = 16;
        static final int SIZE = 46;
        static final int TEMPERATURE = 252;

        // 공백 유지가 필요한 파라미터 ID 목록
        static final Set<Integer> PRESERVE_WHITESPACE_PARAMS = Set.of(PACKAGING, SIZE, TEMPERATURE);
    }

    private static final Map<Integer, String> PARAMETER_FIELD_MAP = createParameterFieldMap();

    /**
     * createParameterFieldMap 메서드는 전자 부품의 파라미터 ID와 검색 필드를 매핑하는
     * 정적 맵을 생성합니다.
     *
     * @return 파라미터 ID(Integer)와 검색 필드(String) 간의 매핑을 담고 있는 맵
     */
    private static Map<Integer, String> createParameterFieldMap() {
        return Map.ofEntries(
                Map.entry(ParameterIds.WATT, PcbPartsSearchField.WATT),
                Map.entry(ParameterIds.TOLERANCE, PcbPartsSearchField.TOLERANCE),
                Map.entry(ParameterIds.OHM, PcbPartsSearchField.OHM),
                Map.entry(ParameterIds.CONDENSER, PcbPartsSearchField.CONDENSER),
                Map.entry(ParameterIds.VOLTAGE, PcbPartsSearchField.VOLTAGE),
                Map.entry(ParameterIds.CURRENT, PcbPartsSearchField.CURRENT),
                Map.entry(ParameterIds.INDUCTOR, PcbPartsSearchField.INDUCTOR),
                Map.entry(ParameterIds.PACKAGING, PcbPartsSearchField.PACKAGING),
                Map.entry(ParameterIds.SIZE, PcbPartsSearchField.SIZE),
                Map.entry(ParameterIds.TEMPERATURE, PcbPartsSearchField.TEMPERATURE)
        );
    }

    /**
     * 주어진 맵에서 제품 정보를 파싱하여 PcbPartsSearch 객체를 생성하고 반환합니다.
     *
     * @param root 제품 정보를 포함한 맵으로, 키는 "Product"입니다.
     * @return 파싱된 제품 정보를 담고 있는 CCObjectResult<PcbPartsSearch> 객체
     */
    public CCObjectResult<PcbPartsSearch> parseProduct(Map<String, Object> root) {
        Map<String, Object> product = (Map) root.get("Product");
        PcbPartsSearch pcbParts = new PcbPartsSearch();

        setBasicInfo(pcbParts, product);
        setCategoryInfo(pcbParts, product);
        setPriceInfo(pcbParts, product);
        setParameterInfo(pcbParts, product);

        return CCObjectResult.setSimpleData(pcbParts);
    }

    public CCResult parseProductsFirst(Map<String, Object> root) {
        List list = (List) root.get("Products");
        if (CollectionUtils.isEmpty(list)) {
            return CCResult.dataNotFound();
        }
        Map<String, Object> product = (Map) list.getFirst();
        PcbPartsSearch pcbParts = new PcbPartsSearch();

        setBasicInfo(pcbParts, product);
        setCategoryInfo(pcbParts, product);
        setPriceInfo(pcbParts, product);
        setParameterInfo(pcbParts, product);

        return CCObjectResult.setSimpleData(pcbParts);
    }

    /**
     * 주어진 매개변수를 처리하여 해당하는 PcbPartsSearch 객체의 속성을 설정합니다.
     *
     * @param pcbParts PcbPartsSearch 객체로, 매개변수에 따라 업데이트됩니다.
     * @param param 매개변수 ID와 값이 포함된 맵 객체이며, 키 "ParameterId"는 정수형이고 키 "ValueText"는 문자열입니다.
     */
    private void processParameter(PcbPartsSearch pcbParts, Map<String, Object> param) {
        int id = (Integer) param.get("ParameterId");
        String value = (String) param.get("ValueText");

        if (!ParameterIds.PRESERVE_WHITESPACE_PARAMS.contains(id)) {
            value = value.replaceAll("\\s+", "");
        }

        String field = PARAMETER_FIELD_MAP.get(id);
        switch (field) {
            case PcbPartsSearchField.SIZE -> pcbParts.setSize(value);
            case PcbPartsSearchField.TEMPERATURE -> pcbParts.setTemperature(value);
            default -> setParameterValue(pcbParts, field, value);
        }
    }

    /**
     * 주어진 제품 정보를 기반으로 PcbPartsSearch 객체의 기본 정보를 설정합니다.
     *
     * @param pcbParts 정보를 설정할 PcbPartsSearch 객체
     * @param product 제품 정보를 포함한 맵 객체
     */
    private void setBasicInfo(PcbPartsSearch pcbParts, Map<String, Object> product) {
        pcbParts.setDescription(getNestedString(product, "Description", "ProductDescription"));
        pcbParts.setManufacturerName(getNestedString(product, "Manufacturer", "Name"));
        pcbParts.setPartName((String) product.get("ManufacturerProductNumber"));
        pcbParts.setPrice((int) ((Number) product.get("UnitPrice")).doubleValue());
        pcbParts.setPhotoUrl((String) product.get("PhotoUrl"));
    }

    /**
     * 주어진 제품 정보를 사용하여 PcbPartsSearch 객체의 카테고리 정보를 설정합니다.
     *
     * @param pcbParts 카테고리 정보를 설정할 PcbPartsSearch 객체
     * @param product 카테고리 정보를 포함하는 제품 정보 맵
     */
    private void setCategoryInfo(PcbPartsSearch pcbParts, Map<String, Object> product) {
        Map<String, Object> category = (Map) product.get("Category");
        pcbParts.setLargeCategory((String) category.get("Name"));

        List<Map<String, Object>> childCategories = (List) category.get("ChildCategories");
        if (!childCategories.isEmpty()) {
            pcbParts.setMediumCategory((String) childCategories.getFirst().get("Name"));

            List<Map<String, Object>> grandChildCategories = (List) childCategories.getFirst().get("ChildCategories");
            if (!grandChildCategories.isEmpty()) {
                pcbParts.setSmallCategory((String) grandChildCategories.getFirst().get("Name"));
            }
        }
    }

    /**
     * 주어진 제품 정보에서 가격 정보를 추출하여 PcbPartsSearch 객체에 설정합니다.
     *
     * @param pcbParts 가격 정보를 설정할 PcbPartsSearch 객체
     * @param product 가격 정보가 포함된 제품 정보 맵
     */
    private void setPriceInfo(PcbPartsSearch pcbParts, Map<String, Object> product) {
        List<Map<String, Object>> variations = (List) product.get("ProductVariations");
        List<PcbPartsPriceSearch> priceSearches = variations.stream()
                .map(this::createPriceSearch)
                .toList();
        pcbParts.setPrices(priceSearches);
    }

    /**
     * 주어진 변형(Map)으로부터 PcbPartsPriceSearch 객체를 생성합니다.
     *
     * @param variation PCB 부품의 다양한 세부 항목과 관련된 정보를 포함하는 Map 객체
     * @return 생성된 PcbPartsPriceSearch 객체
     */
    private PcbPartsPriceSearch createPriceSearch(Map<String, Object> variation) {
        PcbPartsPriceSearch priceSearch = new PcbPartsPriceSearch();
        priceSearch.setSku((String) variation.get("DigiKeyProductNumber"));
        priceSearch.setPkg(getNestedString(variation, "PackageType", "Name"));
        priceSearch.setMoq((Integer) variation.get("MinimumOrderQuantity"));
        priceSearch.setStock((Integer) variation.get("QuantityAvailableforPackageType"));

        List<Map<String, Object>> standardPricing = (List) variation.get("StandardPricing");
        List<PcbPartsPriceStepSearch> priceSteps = standardPricing.stream()
                .map(this::createPriceStep)
                .collect(Collectors.toList());
        priceSearch.setPriceSteps(priceSteps);

        return priceSearch;
    }

    /**
     * 지정된 가격 정보를 기반으로 PcbPartsPriceStepSearch 객체를 생성합니다.
     *
     * @param pricing 가격 정보를 포함하는 맵 객체
     *               - "BreakQuantity": 단계별 수량 제한을 나타내는 키
     *               - "UnitPrice": 단가를 나타내는 키
     * @return 생성된 PcbPartsPriceStepSearch 객체
     */
    private PcbPartsPriceStepSearch createPriceStep(Map<String, Object> pricing) {
        PcbPartsPriceStepSearch step = new PcbPartsPriceStepSearch();
        step.setBreakQuantity((Integer) pricing.get("BreakQuantity"));
        step.setUnitPrice(((Number) pricing.get("UnitPrice")).intValue());
        return step;
    }

    /**
     * 주어진 제품 정보에서 매개변수 정보를 추출하여 PcbPartsSearch 객체에 설정합니다.
     *
     * @param pcbParts PcbPartsSearch 객체, 매개변수 정보가 설정될 대상 객체
     * @param product 제품 정보가 포함된 맵, 매개변수 정보를 추출하기 위한 데이터 소스
     */
    private void setParameterInfo(PcbPartsSearch pcbParts, Map<String, Object> product) {
        List<Map<String, Object>> parameters = (List) product.get("Parameters");
        parameters.stream()
                .filter(param -> PARAMETER_FIELD_MAP.containsKey((Integer) param.get("ParameterId")))
                .forEach(param -> processParameter(pcbParts, param));
    }

    /**
     * 주어진 PCB 부품 검색 객체에 대해 지정된 필드에 해당하는 값으로 설정합니다.
     *
     * @param pcbParts 필드 값이 설정될 PcbPartsSearch 객체
     * @param field 설정할 필드를 나타내는 문자열
     * @param value 설정할 값
     */
    private void setParameterValue(PcbPartsSearch pcbParts, String field, String value) {
        PcbUnitSearch unitSearch = PcbPartsUtils.parsingToPcbUnitSearch(field, value);
        switch (field) {
            case PcbPartsSearchField.WATT -> pcbParts.setWatt(unitSearch);
            case PcbPartsSearchField.TOLERANCE -> pcbParts.setTolerance(unitSearch);
            case PcbPartsSearchField.OHM -> pcbParts.setOhm(unitSearch);
            case PcbPartsSearchField.CONDENSER -> pcbParts.setCondenser(unitSearch);
            case PcbPartsSearchField.VOLTAGE -> pcbParts.setVoltage(unitSearch);
            case PcbPartsSearchField.CURRENT -> pcbParts.setCurrent(unitSearch);
            case PcbPartsSearchField.INDUCTOR -> pcbParts.setInductor(unitSearch);
            case PcbPartsSearchField.PACKAGING -> pcbParts.setPackaging(unitSearch);
        }
    }

    /**
     * 주어진 키 배열을 사용하여 중첩된 맵에서 문자열 값을 검색합니다.
     *
     * @param map 값이 중첩되어 있는 맵
     * @param keys 값을 찾기 위해 순서대로 탐색할 키 배열
     * @return 키 배열을 따라가며 찾은 문자열 값, 값을 찾을 수 없거나 문자열이 아닌 경우 null
     */
    public String getNestedString(Map<String, Object> map, String... keys) {
        Object current = map;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map) current).get(key);
            } else {
                return null;
            }
        }
        return (String) current;
    }

    /**
     * 주어진 키 배열을 사용하여 중첩된 맵에서 Number 값을 검색합니다.
     *
     * @param map  값이 중첩되어 있는 맵
     * @param keys 값을 찾기 위해 순서대로 탐색할 키 배열
     * @return 키 배열을 따라가며 찾은 Number 값, 값을 찾을 수 없거나 Number가 아닌 경우 null
     */
    public Number getNestedNumber(Map<String, Object> map, String... keys) {
        Object current = map;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return null;
            }
        }
        return (current instanceof Number) ? (Number) current : null;
    }

    /**
     * 주어진 키 배열을 사용하여 중첩된 맵에서 Integer 값을 검색합니다.
     *
     * @param map  값이 중첩되어 있는 맵
     * @param keys 값을 찾기 위해 순서대로 탐색할 키 배열
     * @return 키 배열을 따라가며 찾은 Integer 값, 값을 찾을 수 없거나 Number가 아닌 경우 null
     */
    public Integer getNestedInteger(Map<String, Object> map, String... keys) {
        Number number = getNestedNumber(map, keys);
        return number != null ? number.intValue() : null;
    }

    /**
     * 주어진 키 배열을 사용하여 중첩된 맵에서 Double 값을 검색합니다.
     *
     * @param map  값이 중첩되어 있는 맵
     * @param keys 값을 찾기 위해 순서대로 탐색할 키 배열
     * @return 키 배열을 따라가며 찾은 Double 값, 값을 찾을 수 없거나 Number가 아닌 경우 null
     */
    public Double getNestedDouble(Map<String, Object> map, String... keys) {
        Number number = getNestedNumber(map, keys);
        return number != null ? number.doubleValue() : null;
    }
}
