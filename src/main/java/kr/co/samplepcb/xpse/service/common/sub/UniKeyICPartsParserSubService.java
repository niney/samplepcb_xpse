package kr.co.samplepcb.xpse.service.common.sub;

import kr.co.samplepcb.xpse.config.ApplicationProperties;
import kr.co.samplepcb.xpse.domain.document.PcbPartsPriceSearch;
import kr.co.samplepcb.xpse.domain.document.PcbPartsPriceStepSearch;
import kr.co.samplepcb.xpse.domain.document.PcbPartsSearch;
import kr.co.samplepcb.xpse.pojo.PcbPkgType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class UniKeyICPartsParserSubService {

    private final int exchangeRate;

    public UniKeyICPartsParserSubService(ApplicationProperties applicationProperties) {
        this.exchangeRate = applicationProperties.getUnikeyic().getExchangeRate();
    }

    /**
     * UniKeyIC API 응답에서 products 목록을 파싱하여 PcbPartsSearch 리스트로 변환합니다.
     *
     * @param root API 응답 (data.products 포함)
     * @return PcbPartsSearch 리스트
     */
    public List<PcbPartsSearch> parseProducts(Map<String, Object> root) {
        String errCode = (String) root.get("err_code");
        if (errCode != null && !"Com:Success".equals(errCode)) {
            return Collections.emptyList();
        }
        Map<String, Object> data = (Map) root.get("data");
        if (data == null) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> products = (List) data.get("products");
        if (CollectionUtils.isEmpty(products)) {
            return Collections.emptyList();
        }

        List<PcbPartsSearch> results = new ArrayList<>();
        for (Map<String, Object> product : products) {
            PcbPartsSearch pcbParts = new PcbPartsSearch();
            setBasicInfo(pcbParts, product);
            setPriceInfo(pcbParts, product);
            results.add(pcbParts);
        }
        return results;
    }

    private void setBasicInfo(PcbPartsSearch pcbParts, Map<String, Object> product) {
        pcbParts.setServiceType(PcbPkgType.UNIKEYIC.getValue());
        pcbParts.setPartName((String) product.get("pro_sno"));
        pcbParts.setManufacturerName((String) product.get("std_mfr_name"));
        pcbParts.setDescription((String) product.get("short_desc"));
        pcbParts.setDateCode((String) product.get("dc"));
        pcbParts.setPhotoUrl((String) product.get("img_url"));
        pcbParts.setDatasheetUrl((String) product.get("datasheet_url"));
        pcbParts.setLargeCategory((String) product.get("cate_name"));
        pcbParts.setPartsPackaging((String) product.get("package"));

        Number moq = (Number) product.get("moq");
        if (moq != null) {
            pcbParts.setMoq(moq.intValue());
        }
    }

    private void setPriceInfo(PcbPartsSearch pcbParts, Map<String, Object> product) {
        PcbPartsPriceSearch priceSearch = new PcbPartsPriceSearch();
        priceSearch.setDistributor("UniKeyIC");
        priceSearch.setSku((String) product.get("sku"));
        priceSearch.setPkg((String) product.get("package"));

        Number stock = (Number) product.get("stock");
        if (stock != null) {
            priceSearch.setStock(stock.intValue());
        }

        Number moq = (Number) product.get("moq");
        if (moq != null) {
            priceSearch.setMoq(moq.intValue());
        }

        List<PcbPartsPriceStepSearch> priceSteps = parsePriceSteps(product);
        priceSearch.setPriceSteps(priceSteps);

        pcbParts.setPrices(List.of(priceSearch));
    }

    private List<PcbPartsPriceStepSearch> parsePriceSteps(Map<String, Object> product) {
        List calcPrices = (List) product.get("calc_sale_usd_price");
        List nums = (List) product.get("nums");

        if (CollectionUtils.isEmpty(calcPrices) || CollectionUtils.isEmpty(nums)) {
            return Collections.emptyList();
        }

        int size = Math.min(calcPrices.size(), nums.size());
        List<PcbPartsPriceStepSearch> steps = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            PcbPartsPriceStepSearch step = new PcbPartsPriceStepSearch();

            Number quantity = (Number) nums.get(i);
            step.setBreakQuantity(quantity != null ? quantity.intValue() : 0);

            Object priceObj = calcPrices.get(i);
            if (priceObj instanceof Number priceNum) {
                // USD → KRW 변환: USD * exchangeRate → int(원)
                step.setUnitPrice((int) Math.round(priceNum.doubleValue() * exchangeRate));
            } else if (priceObj instanceof Map priceMap) {
                // calc_sale_usd_price가 객체인 경우 (가격 값 추출 시도)
                Number priceVal = findPriceValue(priceMap);
                if (priceVal != null) {
                    step.setUnitPrice((int) (priceVal.doubleValue() * exchangeRate));
                }
            }

            steps.add(step);
        }
        return steps;
    }

    private Number findPriceValue(Map<String, Object> priceMap) {
        // 가격 맵에서 숫자 값 추출 (키 이름이 불명확하므로 첫 번째 Number 타입 값 사용)
        for (Object value : priceMap.values()) {
            if (value instanceof Number) {
                return (Number) value;
            }
        }
        return null;
    }
}
