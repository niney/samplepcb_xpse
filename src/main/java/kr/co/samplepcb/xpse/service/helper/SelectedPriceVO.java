package kr.co.samplepcb.xpse.service.helper;

/**
 * partner_estimate_item.selected_price JSON 의 비교/직렬화용 record.
 */
public record SelectedPriceVO(
        int unitPrice,
        int breakQuantity,
        String pkg,
        int moq,
        int stock,
        int qty
) {}
