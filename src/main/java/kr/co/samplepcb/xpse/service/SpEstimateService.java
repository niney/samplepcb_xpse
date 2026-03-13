package kr.co.samplepcb.xpse.service;

import coolib.common.CCObjectResult;
import coolib.common.CCPagingResult;
import coolib.common.CCResult;
import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import kr.co.samplepcb.xpse.domain.entity.G5ShopItem;
import kr.co.samplepcb.xpse.domain.entity.SpEstimateDocument;
import kr.co.samplepcb.xpse.domain.entity.SpFile;
import kr.co.samplepcb.xpse.domain.entity.SpEstimateItem;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerEstimateItem;
import kr.co.samplepcb.xpse.mapper.SpEstimateMapper;
import kr.co.samplepcb.xpse.pojo.SpEstimateCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateListDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateSearchParam;
import kr.co.samplepcb.xpse.pojo.SpItemCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemCreateDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemListDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemSearchParam;
import kr.co.samplepcb.xpse.pojo.adapter.PagingAdapter;
import kr.co.samplepcb.xpse.repository.G5ShopCartRepository;
import kr.co.samplepcb.xpse.repository.G5ShopItemRepository;
import kr.co.samplepcb.xpse.repository.SpEstimateDocumentRepository;
import kr.co.samplepcb.xpse.repository.SpEstimateItemRepository;
import kr.co.samplepcb.xpse.repository.SpFileRepository;
import kr.co.samplepcb.xpse.repository.SpPartnerEstimateItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SpEstimateService {

    private static final Logger log = LoggerFactory.getLogger(SpEstimateService.class);
    private static final String DEFAULT_STATUS = "협력사 견적요청";
    private static final String PARTNER_ESTIMATE_ITEM_REF_TYPE = "partner_estimate_item";

    private final G5ShopItemRepository shopItemRepository;
    private final G5ShopCartRepository shopCartRepository;
    private final SpEstimateDocumentRepository estimateDocumentRepository;
    private final SpEstimateItemRepository estimateItemRepository;
    private final SpPartnerEstimateItemRepository partnerEstimateItemRepository;
    private final SpFileRepository spFileRepository;
    private final SpEstimateMapper spEstimateMapper;

    public SpEstimateService(G5ShopItemRepository shopItemRepository,
                             G5ShopCartRepository shopCartRepository,
                             SpEstimateDocumentRepository estimateDocumentRepository,
                             SpEstimateItemRepository estimateItemRepository,
                             SpPartnerEstimateItemRepository partnerEstimateItemRepository,
                             SpFileRepository spFileRepository,
                             SpEstimateMapper spEstimateMapper) {
        this.shopItemRepository = shopItemRepository;
        this.shopCartRepository = shopCartRepository;
        this.estimateDocumentRepository = estimateDocumentRepository;
        this.estimateItemRepository = estimateItemRepository;
        this.partnerEstimateItemRepository = partnerEstimateItemRepository;
        this.spFileRepository = spFileRepository;
        this.spEstimateMapper = spEstimateMapper;
    }

    /**
     * 상품 + 장바구니 + 견적서 + 견적항목 일괄 생성.
     * 1) G5ShopItem upsert
     * 2) G5ShopCart upsert (1:1)
     * 3) SpEstimateDocument + SpEstimateItem cascade save
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public CCObjectResult<SpEstimateDetailDTO> create(SpEstimateCreateDTO createDTO, String mbId, String ipAddress) {

        // ── 1. 상품 upsert (SpItemCreateDTO 위임) ──
        SpItemCreateDTO itemDTO = createDTO.toItemCreateDTO();
        if (itemDTO.getItId() == null || itemDTO.getItId().isBlank()) {
            itemDTO.setItId(String.valueOf(System.currentTimeMillis()));
        }
        // DTO에도 생성된 itId를 반영
        createDTO.setItId(itemDTO.getItId());

        Optional<G5ShopItem> optItem = shopItemRepository.findById(itemDTO.getItId());
        G5ShopItem item;
        if (optItem.isPresent()) {
            item = optItem.get();
            itemDTO.applyTo(item);
        } else {
            item = itemDTO.toG5ShopItem(ipAddress);
        }
        G5ShopItem savedItem = shopItemRepository.save(item);

        // ── 2. 장바구니 upsert (1:1) ──
        Optional<G5ShopCart> optCart = shopCartRepository.findByItId(itemDTO.getItId());
        if (optCart.isPresent()) {
            G5ShopCart cart = optCart.get();
            itemDTO.applyTo(cart);
            shopCartRepository.save(cart);
        } else {
            G5ShopCart cart = itemDTO.toG5ShopCart(mbId, ipAddress);
            shopCartRepository.save(cart);
        }

        // ── 3. 견적서 upsert ──
        Date now = new Date();
        Optional<SpEstimateDocument> optDoc = estimateDocumentRepository.findByItId(itemDTO.getItId());
        SpEstimateDocument doc;
        if (optDoc.isPresent()) {
            doc = optDoc.get();
            doc.setModifyDate(now);
        } else {
            doc = new SpEstimateDocument();
            doc.setItId(itemDTO.getItId());
            doc.setWriteDate(now);
            doc.setModifyDate(now);
        }
        spEstimateMapper.updateDocument(createDTO, doc);

        // ── 4. 견적 항목 upsert (기존 항목 교체) ──
        doc.getItems().clear();
        List<SpEstimateCreateDTO.EstimateItemDTO> itemDTOs = createDTO.getItems();
        if (itemDTOs != null) {
            for (SpEstimateCreateDTO.EstimateItemDTO eiDTO : itemDTOs) {
                SpEstimateItem ei = spEstimateMapper.toEstimateItemEntity(eiDTO);
                ei.setEstimateDocument(doc);
                ei.setWriteDate(now);
                ei.setModifyDate(now);
                doc.getItems().add(ei);
            }
        }

        SpEstimateDocument savedDoc = estimateDocumentRepository.save(doc);

        // ── 5. 첨부파일 upsert (기존 파일 교체) ──
        spFileRepository.deleteByRefTypeAndRefId("estimate_document", savedDoc.getId());
        List<SpFile> savedFiles = new ArrayList<>();
        List<SpEstimateCreateDTO.FileDTO> fileDTOs = createDTO.getFiles();
        if (fileDTOs != null) {
            List<SpFile> spFiles = new ArrayList<>();
            for (SpEstimateCreateDTO.FileDTO fDTO : fileDTOs) {
                SpFile sf = spEstimateMapper.toFileEntity(fDTO);
                sf.setRefType("estimate_document");
                sf.setRefId(savedDoc.getId());
                sf.setWriteDate(now);
                spFiles.add(sf);
            }
            savedFiles = spFileRepository.saveAll(spFiles);
        }

        return CCObjectResult.setSimpleData(spEstimateMapper.toDetailDTO(savedDoc, savedFiles));
    }

    /**
     * 견적서 상세 조회 (PK).
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public CCObjectResult<SpEstimateDetailDTO> getDetail(Long id) {
        Optional<SpEstimateDocument> optDoc = estimateDocumentRepository.findDetailById(id);
        if (optDoc.isEmpty()) {
            return dataNotFound();
        }
        SpEstimateDocument doc = optDoc.get();
        List<SpFile> files = spFileRepository.findByRefTypeAndRefId("estimate_document", doc.getId());
        return CCObjectResult.setSimpleData(spEstimateMapper.toDetailDTO(doc, files));
    }

    /**
     * 견적서 상세 조회 (itId).
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public CCObjectResult<SpEstimateDetailDTO> getDetailByItId(String itId) {
        Optional<SpEstimateDocument> optDoc = estimateDocumentRepository.findDetailByItId(itId);
        if (optDoc.isEmpty()) {
            return dataNotFound();
        }
        SpEstimateDocument doc = optDoc.get();
        List<SpFile> files = spFileRepository.findByRefTypeAndRefId("estimate_document", doc.getId());
        return CCObjectResult.setSimpleData(spEstimateMapper.toDetailDTO(doc, files));
    }

    /**
     * 견적서 목록 검색 (페이징).
     */
    @Transactional(readOnly = true)
    public CCPagingResult<SpEstimateListDTO> search(Pageable pageable, SpEstimateSearchParam searchParam) {
        List<SpEstimateListDTO> dtoList = estimateDocumentRepository.findEstimateList(pageable, searchParam);
        long totalCount = estimateDocumentRepository.countEstimateList(searchParam);
        return PagingAdapter.toCCPagingResult(searchParam.getQ(), pageable, dtoList, totalCount);
    }

    /**
     * 견적서 삭제 (문서 + 항목 cascade + 파일).
     */
    @Transactional
    public CCResult delete(Long id) {
        Optional<SpEstimateDocument> optDoc = estimateDocumentRepository.findById(id);
        if (optDoc.isEmpty()) {
            return CCResult.dataNotFound();
        }
        spFileRepository.deleteByRefTypeAndRefId("estimate_document", id);
        estimateDocumentRepository.delete(optDoc.get());
        return CCResult.ok();
    }

    /**
     * 견적서 상태 변경.
     */
    @Transactional
    public CCResult updateStatus(Long id, String status) {
        Optional<SpEstimateDocument> optDoc = estimateDocumentRepository.findById(id);
        if (optDoc.isEmpty()) {
            return CCResult.dataNotFound();
        }
        SpEstimateDocument doc = optDoc.get();
        doc.setStatus(status);
        doc.setModifyDate(new Date());
        estimateDocumentRepository.save(doc);
        return CCResult.ok();
    }

    /**
     * 협력사 견적 항목 등록/수정 (upsert by estimateItemId + mbNo).
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public CCObjectResult<SpPartnerEstimateItem> createPartnerEstimateItem(Long estimateItemId, SpPartnerEstimateItemCreateDTO createDTO) {
        Optional<SpEstimateItem> optItem = estimateItemRepository.findById(estimateItemId);
        if (optItem.isEmpty()) {
            return dataNotFound();
        }
        SpEstimateItem estimateItem = optItem.get();
        Date now = new Date();

        Optional<SpPartnerEstimateItem> optPartner =
                partnerEstimateItemRepository.findByEstimateItemIdAndMbNo(estimateItemId, createDTO.getMbNo());
        SpPartnerEstimateItem partner;
        if (optPartner.isPresent()) {
            partner = optPartner.get();
            partner.setModifyDate(now);
        } else {
            partner = new SpPartnerEstimateItem();
            partner.setEstimateItem(estimateItem);
            partner.setMbNo(createDTO.getMbNo());
            partner.setWriteDate(now);
            partner.setModifyDate(now);
        }
        partner.setSelectedPrice(createDTO.getSelectedPrice());
        String status = createDTO.getStatus();
        partner.setStatus(status == null || status.isBlank() ? DEFAULT_STATUS : status);
        partner.setMemo(createDTO.getMemo());
        partnerEstimateItemRepository.save(partner);
        return CCObjectResult.setSimpleData(partner);
    }

    /**
     * 협력사 주문 단건 생성 (estimateItemId를 DTO에서 추출).
     */
    @Transactional
    public CCResult createPartnerOrder(SpPartnerEstimateItemCreateDTO createDTO) {
        return createPartnerEstimateItem(createDTO.getEstimateItemId(), createDTO);
    }

    /**
     * 협력사 주문 다중 생성.
     */
    @Transactional
    public CCResult createPartnerOrderBatch(List<SpPartnerEstimateItemCreateDTO> createDTOs) {
        for (SpPartnerEstimateItemCreateDTO dto : createDTOs) {
            createPartnerEstimateItem(dto.getEstimateItemId(), dto);
        }
        return CCObjectResult.setSimpleData(createDTOs);
    }

    /**
     * 협력사 견적 항목 상세 조회.
     */
    @Transactional(readOnly = true)
    public CCResult getPartnerEstimateItemDetail(Long estimateItemId, int mbNo) {
        SpPartnerEstimateItemDetailDTO dto = partnerEstimateItemRepository.findPartnerEstimateItemDetail(estimateItemId, mbNo);
        if (dto == null) {
            return CCResult.dataNotFound();
        }
        // 첨부파일 조회
        Optional<SpPartnerEstimateItem> optPartner = partnerEstimateItemRepository.findByEstimateItemIdAndMbNo(estimateItemId, mbNo);
        if (optPartner.isPresent()) {
            List<SpFile> files = spFileRepository.findByRefTypeAndRefId(PARTNER_ESTIMATE_ITEM_REF_TYPE, optPartner.get().getId());
            dto.setFiles(files.stream().map(f -> {
                SpPartnerEstimateItemDetailDTO.FileDTO fileDTO = new SpPartnerEstimateItemDetailDTO.FileDTO();
                fileDTO.setId(f.getId());
                fileDTO.setUploadFileName(f.getUploadFileName());
                fileDTO.setOriginFileName(f.getOriginFileName());
                fileDTO.setPathToken(f.getPathToken());
                fileDTO.setSize(f.getSize());
                fileDTO.setWriteDate(f.getWriteDate());
                return fileDTO;
            }).collect(Collectors.toList()));
        }
        return CCObjectResult.setSimpleData(dto);
    }

    /**
     * 협력사 견적 항목 검색 (페이징).
     */
    @Transactional(readOnly = true)
    public CCResult searchPartnerEstimateItems(Pageable pageable, SpPartnerEstimateItemSearchParam searchParam) {
        List<SpPartnerEstimateItemListDTO> list = partnerEstimateItemRepository.findPartnerEstimateItemList(pageable, searchParam);
        long total = partnerEstimateItemRepository.countPartnerEstimateItemList(searchParam);

        Page<SpPartnerEstimateItemListDTO> dtoPage = new PageImpl<>(list, pageable, total);
        return PagingAdapter.toCCPagingResult(searchParam.getQ(), pageable, dtoPage);
    }

    /**
     * 협력사 견적 선택 (SpEstimateItem.selectedPartnerEstimateItem 설정).
     */
    @Transactional
    public CCResult selectPartnerEstimateItem(Long estimateItemId, Long partnerEstimateItemId) {
        Optional<SpEstimateItem> optItem = estimateItemRepository.findById(estimateItemId);
        if (optItem.isEmpty()) {
            return CCResult.dataNotFound();
        }
        SpEstimateItem estimateItem = optItem.get();

        Optional<SpPartnerEstimateItem> optPartner = partnerEstimateItemRepository.findById(partnerEstimateItemId);
        if (optPartner.isEmpty()) {
            return CCResult.dataNotFound();
        }

        estimateItem.setSelectedPartnerEstimateItem(optPartner.get());
        estimateItem.setModifyDate(new Date());
        estimateItemRepository.save(estimateItem);
        return CCResult.ok();
    }

    @SuppressWarnings("unchecked")
    private static <T> CCObjectResult<T> dataNotFound() {
        CCResult result = CCResult.dataNotFound();
        CCObjectResult<T> objectResult = new CCObjectResult<>();
        objectResult.setResult(result.isResult());
        objectResult.setMessage(result.getMessage());
        return objectResult;
    }
}
