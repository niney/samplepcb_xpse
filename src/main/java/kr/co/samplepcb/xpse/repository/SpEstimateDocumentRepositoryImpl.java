package kr.co.samplepcb.xpse.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.samplepcb.xpse.domain.entity.QG5Member;
import kr.co.samplepcb.xpse.domain.entity.QG5ShopCart;
import kr.co.samplepcb.xpse.domain.entity.QSpEstimateDocument;
import kr.co.samplepcb.xpse.domain.entity.QSpEstimateItem;
import kr.co.samplepcb.xpse.domain.entity.QPcbParts;
import kr.co.samplepcb.xpse.domain.entity.QSpPartnerEstimateDocument;
import kr.co.samplepcb.xpse.domain.entity.QSpPartnerEstimateItem;
import kr.co.samplepcb.xpse.domain.entity.SpEstimateDocument;
import kr.co.samplepcb.xpse.pojo.SpEstimateListDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateSearchParam;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateDocDetailDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;

import kr.co.samplepcb.xpse.domain.entity.SpPartnerEstimateDocument;
import kr.co.samplepcb.xpse.domain.entity.QSpPartnerOrderDocument;
import kr.co.samplepcb.xpse.domain.entity.SpPartnerOrderDocument;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpEstimateDocumentRepositoryImpl implements SpEstimateDocumentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SpEstimateDocumentRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<SpEstimateListDTO> findEstimateList(Pageable pageable, SpEstimateSearchParam searchParam) {
        QSpEstimateDocument doc = QSpEstimateDocument.spEstimateDocument;
        QG5ShopCart cart = QG5ShopCart.g5ShopCart;
        QG5Member member = QG5Member.g5Member;
        QSpEstimateItem item = QSpEstimateItem.spEstimateItem;

        BooleanBuilder where = buildSearchCondition(searchParam, doc);

        return queryFactory
                .select(Projections.bean(SpEstimateListDTO.class,
                        doc.id,
                        doc.itId,
                        cart.itName,
                        doc.status,
                        doc.expectedDelivery,
                        doc.totalAmount,
                        doc.finalAmount,
                        doc.memo,
                        doc.globalMarginRate,
                        doc.writeDate,
                        doc.modifyDate,
                        member.mbId,
                        member.mbName,
                        member.mbEmail,
                        member.mbHp,
                        member.mbTel,
                        ExpressionUtils.as(
                                JPAExpressions.select(item.id.count().intValue())
                                        .from(item)
                                        .where(item.estimateDocument.eq(doc)),
                                "itemCount")
                ))
                .from(doc)
                .leftJoin(cart).on(cart.itId.eq(doc.itId))
                .leftJoin(cart.member, member)
                .where(where)
                .orderBy(doc.writeDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countEstimateList(SpEstimateSearchParam searchParam) {
        QSpEstimateDocument doc = QSpEstimateDocument.spEstimateDocument;

        BooleanBuilder where = buildSearchCondition(searchParam, doc);

        Long count = queryFactory
                .select(doc.count())
                .from(doc)
                .where(where)
                .fetchOne();
        return count != null ? count : 0L;
    }

    private BooleanBuilder buildSearchCondition(SpEstimateSearchParam searchParam, QSpEstimateDocument doc) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(searchParam.getItId())) {
            builder.and(doc.itId.eq(searchParam.getItId()));
        }
        if (StringUtils.isNotBlank(searchParam.getStatus())) {
            builder.and(doc.status.eq(searchParam.getStatus()));
        }

        return builder;
    }

    @Override
    public Optional<SpEstimateDocument> findDetailById(Long id) {
        QSpEstimateDocument doc = QSpEstimateDocument.spEstimateDocument;
        QSpEstimateItem item = QSpEstimateItem.spEstimateItem;

        SpEstimateDocument result = queryFactory
                .selectFrom(doc)
                .leftJoin(doc.items, item).fetchJoin()
                .leftJoin(item.pcbPart).fetchJoin()
                .leftJoin(doc.shopItem).fetchJoin()
                .leftJoin(doc.shopCart).fetchJoin()
                .leftJoin(doc.shopCart.member).fetchJoin()
                .where(doc.id.eq(id))
                .orderBy(item.id.asc())
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<SpEstimateDocument> findDetailByItId(String itId) {
        QSpEstimateDocument doc = QSpEstimateDocument.spEstimateDocument;
        QSpEstimateItem item = QSpEstimateItem.spEstimateItem;

        SpEstimateDocument result = queryFactory
                .selectFrom(doc)
                .leftJoin(doc.items, item).fetchJoin()
                .leftJoin(item.pcbPart).fetchJoin()
                .leftJoin(item.selectedPartnerEstimateItem).fetchJoin()
                .leftJoin(doc.shopItem).fetchJoin()
                .leftJoin(doc.shopCart).fetchJoin()
                .leftJoin(doc.shopCart.member).fetchJoin()
                .where(doc.itId.eq(itId))
                .orderBy(item.id.asc())
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public List<SpEstimateListDTO> findEstimateListForPartner(Pageable pageable, SpEstimateSearchParam searchParam, int mbNo) {
        QSpEstimateDocument doc = QSpEstimateDocument.spEstimateDocument;
        QG5ShopCart cart = QG5ShopCart.g5ShopCart;
        QG5Member member = QG5Member.g5Member;
        QSpEstimateItem item = QSpEstimateItem.spEstimateItem;
        QSpPartnerEstimateDocument ped = QSpPartnerEstimateDocument.spPartnerEstimateDocument;

        BooleanBuilder where = buildSearchCondition(searchParam, doc);
        where.and(ped.mbNo.eq(mbNo));

        return queryFactory
                .select(Projections.bean(SpEstimateListDTO.class,
                        doc.id,
                        doc.itId,
                        cart.itName,
                        doc.status,
                        doc.expectedDelivery,
                        doc.totalAmount,
                        doc.finalAmount,
                        doc.memo,
                        doc.writeDate,
                        doc.modifyDate,
                        member.mbId,
                        member.mbName,
                        member.mbEmail,
                        member.mbHp,
                        member.mbTel,
                        ExpressionUtils.as(
                                JPAExpressions.select(item.id.count().intValue())
                                        .from(item)
                                        .where(item.estimateDocument.eq(doc)),
                                "itemCount")
                ))
                .from(doc)
                .innerJoin(ped).on(ped.estimateDocument.eq(doc))
                .leftJoin(cart).on(cart.itId.eq(doc.itId))
                .leftJoin(cart.member, member)
                .where(where)
                .orderBy(doc.writeDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countEstimateListForPartner(SpEstimateSearchParam searchParam, int mbNo) {
        QSpEstimateDocument doc = QSpEstimateDocument.spEstimateDocument;
        QSpPartnerEstimateDocument ped = QSpPartnerEstimateDocument.spPartnerEstimateDocument;

        BooleanBuilder where = buildSearchCondition(searchParam, doc);
        where.and(ped.mbNo.eq(mbNo));

        Long count = queryFactory
                .select(doc.count())
                .from(doc)
                .innerJoin(ped).on(ped.estimateDocument.eq(doc))
                .where(where)
                .fetchOne();
        return count != null ? count : 0L;
    }

    @Override
    public List<SpEstimateListDTO> findEstimateListWithPartners(Pageable pageable, SpEstimateSearchParam searchParam) {
        List<SpEstimateListDTO> dtoList = findEstimateList(pageable, searchParam);
        if (dtoList.isEmpty()) {
            return dtoList;
        }

        List<Long> docIds = dtoList.stream()
                .map(SpEstimateListDTO::getId)
                .collect(Collectors.toList());

        QSpPartnerEstimateDocument ped = QSpPartnerEstimateDocument.spPartnerEstimateDocument;
        QG5Member partnerMember = new QG5Member("partnerMember");

        List<SpPartnerEstimateDocument> peds = queryFactory
                .selectFrom(ped)
                .leftJoin(ped.member, partnerMember).fetchJoin()
                .where(ped.estimateDocument.id.in(docIds))
                .fetch();

        Map<Long, List<SpEstimateListDTO.PartnerEstimateDTO>> partnerMap = peds.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEstimateDocument().getId(),
                        Collectors.mapping(this::toPartnerEstimateDTO, Collectors.toList())
                ));

        dtoList.forEach(dto ->
                dto.setPartnerEstimates(partnerMap.getOrDefault(dto.getId(), Collections.emptyList()))
        );

        return dtoList;
    }

    private SpEstimateListDTO.PartnerEstimateDTO toPartnerEstimateDTO(SpPartnerEstimateDocument ped) {
        SpEstimateListDTO.PartnerEstimateDTO dto = new SpEstimateListDTO.PartnerEstimateDTO();
        BeanUtils.copyProperties(ped, dto);
        dto.setPartnerName(ped.getMember() != null ? ped.getMember().getMbName() : null);
        return dto;
    }

    @Override
    public List<SpEstimateListDTO> findEstimateListWithPartnerOrders(Pageable pageable, SpEstimateSearchParam searchParam) {
        List<SpEstimateListDTO> dtoList = findEstimateList(pageable, searchParam);
        if (dtoList.isEmpty()) {
            return dtoList;
        }

        List<Long> docIds = dtoList.stream()
                .map(SpEstimateListDTO::getId)
                .collect(Collectors.toList());

        QSpPartnerOrderDocument pod = QSpPartnerOrderDocument.spPartnerOrderDocument;
        QG5Member partnerMember = new QG5Member("partnerMember");

        List<SpPartnerOrderDocument> pods = queryFactory
                .selectFrom(pod)
                .leftJoin(pod.member, partnerMember).fetchJoin()
                .where(pod.estimateDocument.id.in(docIds))
                .fetch();

        Map<Long, List<SpEstimateListDTO.PartnerOrderDTO>> orderMap = pods.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEstimateDocument().getId(),
                        Collectors.mapping(this::toPartnerOrderDTO, Collectors.toList())
                ));

        dtoList.forEach(dto ->
                dto.setPartnerOrders(orderMap.getOrDefault(dto.getId(), Collections.emptyList()))
        );

        return dtoList;
    }

    private SpEstimateListDTO.PartnerOrderDTO toPartnerOrderDTO(SpPartnerOrderDocument pod) {
        SpEstimateListDTO.PartnerOrderDTO dto = new SpEstimateListDTO.PartnerOrderDTO();
        BeanUtils.copyProperties(pod, dto);
        dto.setPartnerName(pod.getMember() != null ? pod.getMember().getMbName() : null);
        return dto;
    }

    @Override
    public List<SpPartnerEstimateDocDetailDTO.ItemDTO> findDetailItemsForPartner(Long docId, Long pedId) {
        QSpEstimateItem ei = QSpEstimateItem.spEstimateItem;
        QPcbParts pp = QPcbParts.pcbParts;
        QSpPartnerEstimateItem pei = QSpPartnerEstimateItem.spPartnerEstimateItem;

        return queryFactory
                .select(Projections.constructor(SpPartnerEstimateDocDetailDTO.ItemDTO.class,
                        ei.id, ei.pcbPartDocId, ei.qty, ei.analysisMeta, ei.selectedPrice,
                        pp.partName, pp.description, pp.manufacturerName, pp.partsPackaging, pp.size,
                        pp.moq, pp.price, pp.offerName,
                        pei.id, pei.selectedPrice, pei.status,
                        pei.memo, pei.dateCode, pei.deliveryDate,
                        pei.writeDate, pei.modifyDate))
                .from(ei)
                .leftJoin(ei.pcbPart, pp)
                .join(pei).on(pei.estimateItem.eq(ei).and(pei.partnerEstimateDocument.id.eq(pedId)))
                .where(ei.estimateDocument.id.eq(docId))
                .orderBy(ei.id.asc())
                .fetch();
    }
}
