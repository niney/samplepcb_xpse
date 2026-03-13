package kr.co.samplepcb.xpse.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.samplepcb.xpse.domain.entity.QG5Member;
import kr.co.samplepcb.xpse.domain.entity.QG5ShopItem;
import kr.co.samplepcb.xpse.domain.entity.QSpEstimateDocument;
import kr.co.samplepcb.xpse.domain.entity.QSpEstimateItem;
import kr.co.samplepcb.xpse.domain.entity.QSpPartnerEstimateItem;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateDocListDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemListDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerEstimateItemSearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class SpPartnerEstimateItemRepositoryImpl implements SpPartnerEstimateItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SpPartnerEstimateItemRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<SpPartnerEstimateItemListDTO> findPartnerEstimateItemList(Pageable pageable, SpPartnerEstimateItemSearchParam searchParam) {
        QSpPartnerEstimateItem pei = QSpPartnerEstimateItem.spPartnerEstimateItem;
        QSpEstimateItem ei = QSpEstimateItem.spEstimateItem;
        QG5Member member = QG5Member.g5Member;

        BooleanBuilder where = buildSearchCondition(searchParam, pei);

        return queryFactory
                .select(Projections.constructor(SpPartnerEstimateItemListDTO.class,
                        pei.id, ei.id, pei.mbNo,
                        pei.status, pei.memo, pei.selectedPrice,
                        pei.writeDate, pei.modifyDate,
                        member.mbId, member.mbName, member.mbNick,
                        member.mbEmail, member.mbHp))
                .from(pei)
                .leftJoin(pei.estimateItem, ei)
                .leftJoin(pei.member, member)
                .where(where)
                .orderBy(pei.writeDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countPartnerEstimateItemList(SpPartnerEstimateItemSearchParam searchParam) {
        QSpPartnerEstimateItem pei = QSpPartnerEstimateItem.spPartnerEstimateItem;

        BooleanBuilder where = buildSearchCondition(searchParam, pei);

        Long count = queryFactory
                .select(pei.count())
                .from(pei)
                .where(where)
                .fetchOne();
        return count != null ? count : 0L;
    }

    @Override
    public SpPartnerEstimateItemDetailDTO findPartnerEstimateItemDetail(Long estimateItemId, int mbNo) {
        QSpPartnerEstimateItem pei = QSpPartnerEstimateItem.spPartnerEstimateItem;
        QSpEstimateItem ei = QSpEstimateItem.spEstimateItem;
        QG5Member member = QG5Member.g5Member;

        return queryFactory
                .select(Projections.constructor(SpPartnerEstimateItemDetailDTO.class,
                        pei.id, ei.id, pei.mbNo,
                        pei.status, pei.memo, pei.selectedPrice,
                        pei.writeDate, pei.modifyDate,
                        member.mbId, member.mbName, member.mbNick,
                        member.mbEmail, member.mbHp))
                .from(pei)
                .leftJoin(pei.estimateItem, ei)
                .leftJoin(pei.member, member)
                .where(ei.id.eq(estimateItemId), pei.mbNo.eq(mbNo))
                .fetchOne();
    }

    private BooleanBuilder buildSearchCondition(SpPartnerEstimateItemSearchParam searchParam, QSpPartnerEstimateItem pei) {
        BooleanBuilder builder = new BooleanBuilder();

        if (searchParam.getEstimateItemId() != null) {
            builder.and(pei.estimateItem.id.eq(searchParam.getEstimateItemId()));
        }
        if (searchParam.getMbNo() != null) {
            builder.and(pei.mbNo.eq(searchParam.getMbNo()));
        }
        if (StringUtils.isNotBlank(searchParam.getStatus())) {
            builder.and(pei.status.eq(searchParam.getStatus()));
        }

        return builder;
    }

    @Override
    public List<SpPartnerEstimateDocListDTO> findPartnerEstimateDocList(Pageable pageable, SpPartnerEstimateItemSearchParam searchParam) {
        QSpEstimateDocument doc = QSpEstimateDocument.spEstimateDocument;
        QSpEstimateItem ei = QSpEstimateItem.spEstimateItem;
        QSpPartnerEstimateItem pei = QSpPartnerEstimateItem.spPartnerEstimateItem;
        QG5ShopItem shopItem = QG5ShopItem.g5ShopItem;

        // 서브쿼리: 해당 파트너가 참여한 estimate_document ID 목록
        BooleanBuilder subWhere = new BooleanBuilder();
        if (searchParam.getMbNo() != null) {
            subWhere.and(pei.mbNo.eq(searchParam.getMbNo()));
        }
        if (StringUtils.isNotBlank(searchParam.getStatus())) {
            subWhere.and(doc.status.eq(searchParam.getStatus()));
        }

        // 항목 수 서브쿼리
        QSpEstimateItem eiCount = new QSpEstimateItem("eiCount");

        return queryFactory
                .select(Projections.constructor(SpPartnerEstimateDocListDTO.class,
                        doc.id, doc.itId, shopItem.itName,
                        doc.status, doc.expectedDelivery,
                        doc.totalAmount, doc.finalAmount,
                        doc.memo, doc.globalMarginRate,
                        JPAExpressions.select(eiCount.count())
                                .from(eiCount)
                                .where(eiCount.estimateDocument.id.eq(doc.id)),
                        doc.writeDate, doc.modifyDate))
                .from(doc)
                .leftJoin(doc.shopItem, shopItem)
                .where(doc.id.in(
                        JPAExpressions.select(ei.estimateDocument.id)
                                .from(pei)
                                .join(pei.estimateItem, ei)
                                .where(subWhere)
                                .distinct()
                ))
                .orderBy(doc.writeDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countPartnerEstimateDocList(SpPartnerEstimateItemSearchParam searchParam) {
        QSpEstimateDocument doc = QSpEstimateDocument.spEstimateDocument;
        QSpEstimateItem ei = QSpEstimateItem.spEstimateItem;
        QSpPartnerEstimateItem pei = QSpPartnerEstimateItem.spPartnerEstimateItem;

        BooleanBuilder subWhere = new BooleanBuilder();
        if (searchParam.getMbNo() != null) {
            subWhere.and(pei.mbNo.eq(searchParam.getMbNo()));
        }
        if (StringUtils.isNotBlank(searchParam.getStatus())) {
            subWhere.and(doc.status.eq(searchParam.getStatus()));
        }

        Long count = queryFactory
                .select(doc.countDistinct())
                .from(doc)
                .where(doc.id.in(
                        JPAExpressions.select(ei.estimateDocument.id)
                                .from(pei)
                                .join(pei.estimateItem, ei)
                                .where(subWhere)
                                .distinct()
                ))
                .fetchOne();
        return count != null ? count : 0L;
    }
}
