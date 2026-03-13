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
import kr.co.samplepcb.xpse.domain.entity.SpEstimateDocument;
import kr.co.samplepcb.xpse.pojo.SpEstimateListDTO;
import kr.co.samplepcb.xpse.pojo.SpEstimateSearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

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
                .where(doc.id.eq(id))
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
                .leftJoin(doc.shopItem).fetchJoin()
                .where(doc.itId.eq(itId))
                .fetchOne();
        return Optional.ofNullable(result);
    }
}
