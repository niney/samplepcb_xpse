package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import kr.co.samplepcb.xpse.domain.entity.SpEstimateDocument;
import kr.co.samplepcb.xpse.domain.entity.SpEstimateItem;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrderDocument;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrderItem;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderItemCreateDTO;
import kr.co.samplepcb.xpse.repository.SpEstimateItemRepository;
import kr.co.samplepcb.xpse.repository.SpPartnerOrderDocumentRepository;
import kr.co.samplepcb.xpse.repository.SpPartnerOrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SpPartnerOrderService {

    private static final Logger log = LoggerFactory.getLogger(SpPartnerOrderService.class);
    private static final String DEFAULT_STATUS = "발주접수";

    private final SpEstimateItemRepository estimateItemRepository;
    private final SpPartnerOrderDocumentRepository partnerOrderDocumentRepository;
    private final SpPartnerOrderItemRepository partnerOrderItemRepository;

    public SpPartnerOrderService(SpEstimateItemRepository estimateItemRepository,
                                 SpPartnerOrderDocumentRepository partnerOrderDocumentRepository,
                                 SpPartnerOrderItemRepository partnerOrderItemRepository) {
        this.estimateItemRepository = estimateItemRepository;
        this.partnerOrderDocumentRepository = partnerOrderDocumentRepository;
        this.partnerOrderItemRepository = partnerOrderItemRepository;
    }

    /**
     * 협력사 발주 다중 생성 (최적화).
     * EstimateItem+Document JOIN FETCH 일괄 조회 → Document 캐싱 upsert → Item 일괄 저장.
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public CCObjectResult<List<SpPartnerOrderItemCreateDTO>> createPartnerOrderBatch(List<SpPartnerOrderItemCreateDTO> createDTOs) {
        if (createDTOs == null || createDTOs.isEmpty()) {
            return CCObjectResult.setSimpleData(createDTOs);
        }

        Date now = new Date();

        List<Long> estimateItemIds = createDTOs.stream()
                .map(SpPartnerOrderItemCreateDTO::getEstimateItemId)
                .distinct()
                .toList();
        Map<Long, SpEstimateItem> estimateItemMap = estimateItemRepository.findAllByIdWithDocument(estimateItemIds)
                .stream()
                .collect(Collectors.toMap(SpEstimateItem::getId, Function.identity()));

        Map<String, SpPartnerOrderDocument> docCache = new HashMap<>();
        List<SpPartnerOrderItem> itemsToSave = new ArrayList<>();

        for (SpPartnerOrderItemCreateDTO dto : createDTOs) {
            SpEstimateItem estimateItem = estimateItemMap.get(dto.getEstimateItemId());
            if (estimateItem == null) {
                log.warn("EstimateItem not found: {}", dto.getEstimateItemId());
                continue;
            }
            SpEstimateDocument estimateDocument = estimateItem.getEstimateDocument();
            String docKey = estimateDocument.getId() + "_" + dto.getMbNo();

            SpPartnerOrderDocument partnerDoc = docCache.computeIfAbsent(docKey, k ->
                    partnerOrderDocumentRepository
                            .findByEstimateDocumentIdAndMbNo(estimateDocument.getId(), dto.getMbNo())
                            .orElseGet(() -> {
                                SpPartnerOrderDocument newDoc = new SpPartnerOrderDocument();
                                newDoc.setEstimateDocument(estimateDocument);
                                newDoc.setMbNo(dto.getMbNo());
                                newDoc.setStatus(DEFAULT_STATUS);
                                newDoc.setWriteDate(now);
                                newDoc.setModifyDate(now);
                                return partnerOrderDocumentRepository.save(newDoc);
                            })
            );

            SpPartnerOrderItem item = partnerOrderItemRepository
                    .findByEstimateItemIdAndPartnerOrderDocumentId(dto.getEstimateItemId(), partnerDoc.getId())
                    .orElseGet(() -> {
                        SpPartnerOrderItem newItem = new SpPartnerOrderItem();
                        newItem.setEstimateItem(estimateItem);
                        newItem.setPartnerOrderDocument(partnerDoc);
                        newItem.setMbNo(dto.getMbNo());
                        newItem.setWriteDate(now);
                        return newItem;
                    });

            item.setSelectedPrice(dto.getSelectedPrice());
            String status = dto.getStatus();
            item.setStatus(status == null || status.isBlank() ? DEFAULT_STATUS : status);
            item.setMemo(dto.getMemo());
            item.setDateCode(dto.getDateCode());
            item.setDeliveryDate(dto.getDeliveryDate());
            item.setModifyDate(now);
            itemsToSave.add(item);
        }

        partnerOrderItemRepository.saveAll(itemsToSave);

        return CCObjectResult.setSimpleData(createDTOs);
    }
}
