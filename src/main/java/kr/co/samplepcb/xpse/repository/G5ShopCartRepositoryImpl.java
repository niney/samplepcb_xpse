package kr.co.samplepcb.xpse.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.samplepcb.xpse.domain.entity.G5ShopCart;
import kr.co.samplepcb.xpse.domain.entity.QG5Member;
import kr.co.samplepcb.xpse.domain.entity.QG5ShopCart;
import kr.co.samplepcb.xpse.domain.entity.QG5ShopItem;
import kr.co.samplepcb.xpse.domain.entity.QSpPartnerOrder;
import kr.co.samplepcb.xpse.pojo.SpOrderSearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class G5ShopCartRepositoryImpl implements G5ShopCartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public G5ShopCartRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<G5ShopCart> findOrderList(Pageable pageable, SpOrderSearchParam searchParam) {
        QG5ShopCart ca = QG5ShopCart.g5ShopCart;
        QG5ShopItem a = QG5ShopItem.g5ShopItem;
        QG5Member c = QG5Member.g5Member;
        QSpPartnerOrder po = QSpPartnerOrder.spPartnerOrder;

        BooleanBuilder where = buildSearchCondition(searchParam, ca, a, c);

        List<Integer> ids = queryFactory
                .select(ca.ctId)
                .from(ca)
                .leftJoin(ca.shopItem, a)
                .leftJoin(ca.member, c)
                .where(where)
                .orderBy(ca.ctTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .selectFrom(ca)
                .leftJoin(ca.shopItem, a).fetchJoin()
                .leftJoin(a.partnerOrders, po).fetchJoin()
                .leftJoin(ca.member, c).fetchJoin()
                .where(ca.ctId.in(ids))
                .orderBy(ca.ctTime.desc())
                .fetch();
    }

    @Override
    public long countOrderList(SpOrderSearchParam searchParam) {
        QG5ShopCart ca = QG5ShopCart.g5ShopCart;
        QG5ShopItem a = QG5ShopItem.g5ShopItem;
        QG5Member c = QG5Member.g5Member;

        BooleanBuilder where = buildSearchCondition(searchParam, ca, a, c);

        Long count = queryFactory
                .select(ca.count())
                .from(ca)
                .leftJoin(ca.shopItem, a)
                .leftJoin(ca.member, c)
                .where(where)
                .fetchOne();
        return count != null ? count : 0L;
    }

    private BooleanBuilder buildSearchCondition(SpOrderSearchParam searchParam,
                                                 QG5ShopCart ca, QG5ShopItem a, QG5Member c) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotEmpty(searchParam.getCtStatus())) {
            builder.and(ca.ctStatus.eq(searchParam.getCtStatus()));
        }

        if (StringUtils.isNotEmpty(searchParam.getCaId())) {
            builder.and(a.caId.eq(searchParam.getCaId()));
        }

        String q = searchParam.getQ();
        if (StringUtils.isEmpty(q)) {
            return builder;
        }

        String field = searchParam.getField();
        if (StringUtils.isEmpty(field)) {
            builder.andAnyOf(
                    ca.itName.containsIgnoreCase(q),
                    ca.mbId.containsIgnoreCase(q),
                    a.itMaker.containsIgnoreCase(q),
                    a.itModel.containsIgnoreCase(q),
                    a.itBrand.containsIgnoreCase(q),
                    c.mbName.containsIgnoreCase(q),
                    c.mbNick.containsIgnoreCase(q)
            );
            return builder;
        }

        switch (field) {
            case "itName" -> builder.and(ca.itName.containsIgnoreCase(q));
            case "mbId" -> builder.and(ca.mbId.containsIgnoreCase(q));
            case "mbName" -> builder.and(c.mbName.containsIgnoreCase(q));
            case "itMaker" -> builder.and(a.itMaker.containsIgnoreCase(q));
            case "itModel" -> builder.and(a.itModel.containsIgnoreCase(q));
            case "itBrand" -> builder.and(a.itBrand.containsIgnoreCase(q));
        }

        return builder;
    }
}
