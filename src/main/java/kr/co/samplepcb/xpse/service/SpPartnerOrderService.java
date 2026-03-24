package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.entity.SpEstimateDocument;
import kr.co.samplepcb.xpse.domain.entity.SpEstimateItem;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerEstimateDocument;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerEstimateItem;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrderDocument;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrderItem;
import kr.co.samplepcb.xpse.exception.BusinessException;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderItemCreateDTO;
import kr.co.samplepcb.xpse.repository.SpEstimateDocumentRepository;
import kr.co.samplepcb.xpse.repository.SpEstimateItemRepository;
import kr.co.samplepcb.xpse.repository.SpPartnerEstimateDocumentRepository;
import kr.co.samplepcb.xpse.repository.SpPartnerEstimateItemRepository;
import kr.co.samplepcb.xpse.repository.SpPartnerOrderDocumentRepository;
import kr.co.samplepcb.xpse.repository.SpPartnerOrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.json.JsonMapper;

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
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private final SpEstimateDocumentRepository estimateDocumentRepository;
    private final SpEstimateItemRepository estimateItemRepository;
    private final SpPartnerEstimateDocumentRepository partnerEstimateDocumentRepository;
    private final SpPartnerEstimateItemRepository partnerEstimateItemRepository;
    private final SpPartnerOrderDocumentRepository partnerOrderDocumentRepository;
    private final SpPartnerOrderItemRepository partnerOrderItemRepository;

    public SpPartnerOrderService(SpEstimateDocumentRepository estimateDocumentRepository,
                                 SpEstimateItemRepository estimateItemRepository,
                                 SpPartnerEstimateDocumentRepository partnerEstimateDocumentRepository,
                                 SpPartnerEstimateItemRepository partnerEstimateItemRepository,
                                 SpPartnerOrderDocumentRepository partnerOrderDocumentRepository,
                                 SpPartnerOrderItemRepository partnerOrderItemRepository) {
        this.estimateDocumentRepository = estimateDocumentRepository;
        this.estimateItemRepository = estimateItemRepository;
        this.partnerEstimateDocumentRepository = partnerEstimateDocumentRepository;
        this.partnerEstimateItemRepository = partnerEstimateItemRepository;
        this.partnerOrderDocumentRepository = partnerOrderDocumentRepository;
        this.partnerOrderItemRepository = partnerOrderItemRepository;
    }

    /**
     * 협력사 발주서 상세 조회 (발주서 ID).
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public CCObjectResult<SpPartnerOrderDetailDTO> getOrderDetail(Long orderDocId) {
        SpPartnerOrderDocument orderDoc = partnerOrderDocumentRepository.findById(orderDocId)
                .orElse(null);
        if (orderDoc == null) {
            return (CCObjectResult<SpPartnerOrderDetailDTO>) CCResult.dataNotFound();
        }
        return CCObjectResult.setSimpleData(buildOrderDetailDTO(orderDoc));
    }

    /**
     * 협력사 발주서 상세 조회 (itId).
     * 하나의 itId에 여러 협력사 발주서가 있을 수 있으므로 List 반환.
     */
    @Transactional(readOnly = true)
    public CCObjectResult<List<SpPartnerOrderDetailDTO>> getOrderDetailByItId(String itId) {
        List<SpPartnerOrderDocument> orderDocs = partnerOrderDocumentRepository.findByEstimateDocumentItId(itId);
        List<SpPartnerOrderDetailDTO> dtoList = orderDocs.stream()
                .map(this::buildOrderDetailDTO)
                .toList();
        return CCObjectResult.setSimpleData(dtoList);
    }

    private SpPartnerOrderDetailDTO buildOrderDetailDTO(SpPartnerOrderDocument orderDoc) {
        SpEstimateDocument estimateDoc = orderDoc.getEstimateDocument();

        SpPartnerOrderDetailDTO dto = new SpPartnerOrderDetailDTO();
        dto.setEstimateDocumentId(estimateDoc.getId());
        dto.setItId(estimateDoc.getItId());
        dto.setItName(estimateDoc.getShopItem() != null ? estimateDoc.getShopItem().getItName() : null);
        dto.setEstimateStatus(estimateDoc.getStatus());
        dto.setId(orderDoc.getId());
        dto.setMbNo(orderDoc.getMbNo());
        dto.setPartnerName(orderDoc.getMember() != null ? orderDoc.getMember().getMbName() : null);
        dto.setStatus(orderDoc.getStatus());
        dto.setOrderPrice(orderDoc.getOrderPrice());
        dto.setMemo(orderDoc.getMemo());
        dto.setDeliveryDate(orderDoc.getDeliveryDate());
        dto.setWriteDate(orderDoc.getWriteDate());
        dto.setModifyDate(orderDoc.getModifyDate());

        List<SpPartnerOrderDetailDTO.ItemDTO> items = estimateDocumentRepository
                .findDetailItemsForOrder(estimateDoc.getId(), orderDoc.getId());
        dto.setItems(items);

        return dto;
    }

    /**
     * 협력사 발주 다중 생성 (최적화).
     * EstimateItem+Document JOIN FETCH 일괄 조회 → Document 캐싱 upsert → Item 일괄 저장.
     */
    @Transactional
    public CCResult createPartnerOrderBatch(List<SpPartnerOrderItemCreateDTO> createDTOs) {
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

        // orderPrice 계산: 각 Document별 협력사 견적 항목의 unitPrice * qty 합산
        for (SpPartnerOrderDocument partnerDoc : docCache.values()) {
            calculateAndSetOrderPrice(partnerDoc);
        }

        return CCObjectResult.setSimpleData(createDTOs);
    }

    private void calculateAndSetOrderPrice(SpPartnerOrderDocument partnerDoc) {
        SpPartnerEstimateDocument estimateDoc = partnerEstimateDocumentRepository
                .findByEstimateDocumentIdAndMbNo(partnerDoc.getEstimateDocument().getId(), partnerDoc.getMbNo())
                .orElseThrow(() -> new BusinessException("협력사 견적 문서를 찾을 수 없습니다. mbNo=" + partnerDoc.getMbNo()));

        List<SpPartnerEstimateItem> estimateItems = partnerEstimateItemRepository
                .findByPartnerEstimateDocumentId(estimateDoc.getId());

        int totalPrice = 0;
        for (SpPartnerEstimateItem item : estimateItems) {
            SelectedPrice price = parseSelectedPrice(item);
            totalPrice += price.unitPrice() * price.qty();
        }

        partnerDoc.setOrderPrice(totalPrice);
        partnerOrderDocumentRepository.save(partnerDoc);
    }

    private SelectedPrice parseSelectedPrice(SpPartnerEstimateItem item) {
        String json = item.getSelectedPrice();
        if (json == null || json.isBlank()) {
            throw new BusinessException("협력사 견적 항목의 selectedPrice가 비어있습니다. itemId=" + item.getId());
        }
        try {
            SelectedPrice price = JSON_MAPPER.readValue(json, SelectedPrice.class);
            if (price.unitPrice() == null || price.qty() == null) {
                throw new BusinessException("selectedPrice에 unitPrice 또는 qty가 없습니다. itemId=" + item.getId());
            }
            return price;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("selectedPrice JSON 파싱 실패. itemId=" + item.getId() + ", error=" + e.getMessage());
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SelectedPrice(Integer unitPrice, Integer qty) {}
}
