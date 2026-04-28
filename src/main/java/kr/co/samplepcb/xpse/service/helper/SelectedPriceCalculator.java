package kr.co.samplepcb.xpse.service.helper;

import kr.co.samplepcb.xpse.domain.document.PcbPartsPriceSearch;
import kr.co.samplepcb.xpse.domain.document.PcbPartsPriceStepSearch;
import kr.co.samplepcb.xpse.domain.document.PcbPartsSearch;

import java.util.List;
import java.util.Optional;

/**
 * 외부 공급사 PcbParts 응답에서 qty 기준의 SelectedPrice 를 계산하는 유틸.
 * 프론트의 pickPriceStep / pickBestExternalOverride 와 동일한 알고리즘을 따른다.
 */
public class SelectedPriceCalculator {

    public Optional<SelectedPriceVO> calculate(PcbPartsSearch part, int qty) {
        if (part == null || qty <= 0 || part.getPrices() == null || part.getPrices().isEmpty()) {
            return Optional.empty();
        }
        PcbPartsPriceSearch bestRow = null;
        PcbPartsPriceStepSearch bestStep = null;
        for (PcbPartsPriceSearch row : part.getPrices()) {
            PcbPartsPriceStepSearch step = pickStep(row.getPriceSteps(), qty);
            if (step == null || step.getUnitPrice() <= 0) {
                continue;
            }
            if (bestStep == null || step.getUnitPrice() < bestStep.getUnitPrice()) {
                bestRow = row;
                bestStep = step;
            }
        }
        if (bestRow == null) {
            return Optional.empty();
        }
        return Optional.of(new SelectedPriceVO(
                bestStep.getUnitPrice(),
                bestStep.getBreakQuantity(),
                bestRow.getPkg(),
                bestRow.getMoq(),
                bestRow.getStock(),
                qty
        ));
    }

    /**
     * qty 에 적용 가능한(breakQuantity ≤ qty) 가장 큰 breakQuantity step 채택.
     * 적용 가능한 step 이 없으면 가장 작은 breakQuantity step 폴백.
     */
    private PcbPartsPriceStepSearch pickStep(List<PcbPartsPriceStepSearch> steps, int qty) {
        if (steps == null || steps.isEmpty()) {
            return null;
        }
        PcbPartsPriceStepSearch applicable = null;
        for (PcbPartsPriceStepSearch s : steps) {
            if (s.getBreakQuantity() <= qty
                    && (applicable == null || s.getBreakQuantity() > applicable.getBreakQuantity())) {
                applicable = s;
            }
        }
        if (applicable != null) {
            return applicable;
        }
        PcbPartsPriceStepSearch fallback = null;
        for (PcbPartsPriceStepSearch s : steps) {
            if (fallback == null || s.getBreakQuantity() < fallback.getBreakQuantity()) {
                fallback = s;
            }
        }
        return fallback;
    }
}
