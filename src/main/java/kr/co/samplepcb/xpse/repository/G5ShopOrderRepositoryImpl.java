package kr.co.samplepcb.xpse.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.samplepcb.xpse.domain.entity.G5ShopOrder;
import kr.co.samplepcb.xpse.domain.entity.QG5Member;
import kr.co.samplepcb.xpse.domain.entity.QG5ShopCart;
import kr.co.samplepcb.xpse.domain.entity.QG5ShopItem;
import kr.co.samplepcb.xpse.domain.entity.QG5ShopOrder;
import kr.co.samplepcb.xpse.pojo.G5ShopOrderSearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public class G5ShopOrderRepositoryImpl implements G5ShopOrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public G5ShopOrderRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<G5ShopOrder> findOrderList(Pageable pageable, G5ShopOrderSearchParam searchParam) {
        QG5ShopOrder od = QG5ShopOrder.g5ShopOrder;
        QG5Member mb = QG5Member.g5Member;

        BooleanBuilder where = buildSearchCondition(searchParam, od, mb);

        List<Long> ids = queryFactory
                .select(od.odId)
                .from(od)
                .leftJoin(od.member, mb)
                .where(where)
                .orderBy(od.odTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .selectFrom(od)
                .leftJoin(od.member, mb).fetchJoin()
                .leftJoin(od.carts).fetchJoin()
                .where(od.odId.in(ids))
                .orderBy(od.odTime.desc())
                .fetch();
    }

    @Override
    public long countOrderList(G5ShopOrderSearchParam searchParam) {
        QG5ShopOrder od = QG5ShopOrder.g5ShopOrder;
        QG5Member mb = QG5Member.g5Member;

        BooleanBuilder where = buildSearchCondition(searchParam, od, mb);

        Long count = queryFactory
                .select(od.count())
                .from(od)
                .leftJoin(od.member, mb)
                .where(where)
                .fetchOne();
        return count != null ? count : 0L;
    }

    @Override
    public Optional<G5ShopOrder> findOrderByItId(String itId) {
        QG5ShopOrder od = QG5ShopOrder.g5ShopOrder;
        QG5ShopCart cart = QG5ShopCart.g5ShopCart;
        QG5Member mb = QG5Member.g5Member;

        Long odId = queryFactory
                .select(cart.odId)
                .from(cart)
                .where(cart.itId.eq(itId))
                .fetchFirst();

        if (odId == null) {
            return Optional.empty();
        }

        G5ShopOrder order = queryFactory
                .selectFrom(od)
                .leftJoin(od.member, mb).fetchJoin()
                .leftJoin(od.carts).fetchJoin()
                .where(od.odId.eq(odId))
                .fetchOne();

        return Optional.ofNullable(order);
    }

    private BooleanBuilder buildSearchCondition(G5ShopOrderSearchParam searchParam,
                                                 QG5ShopOrder od, QG5Member mb) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotEmpty(searchParam.getOdStatus())) {
            builder.and(od.odStatus.eq(searchParam.getOdStatus()));
        }

        if (StringUtils.isNotEmpty(searchParam.getMbId())) {
            builder.and(od.mbId.eq(searchParam.getMbId()));
        }

        if (StringUtils.isNotEmpty(searchParam.getCaId())) {
            QG5ShopCart cart = QG5ShopCart.g5ShopCart;
            QG5ShopItem item = QG5ShopItem.g5ShopItem;
            builder.and(od.odId.in(
                    JPAExpressions.select(cart.odId)
                            .from(cart)
                            .leftJoin(cart.shopItem, item)
                            .where(item.caId.eq(searchParam.getCaId()))
            ));
        }

        String q = searchParam.getQ();
        if (StringUtils.isEmpty(q)) {
            return builder;
        }

        String field = searchParam.getField();
        if (StringUtils.isEmpty(field)) {
            builder.andAnyOf(
                    od.odName.containsIgnoreCase(q),
                    od.mbId.containsIgnoreCase(q),
                    od.odEmail.containsIgnoreCase(q),
                    mb.mbName.containsIgnoreCase(q),
                    mb.mbNick.containsIgnoreCase(q)
            );
            return builder;
        }

        switch (field) {
            case "odName" -> builder.and(od.odName.containsIgnoreCase(q));
            case "mbId" -> builder.and(od.mbId.containsIgnoreCase(q));
            case "odEmail" -> builder.and(od.odEmail.containsIgnoreCase(q));
            case "mbName" -> builder.and(mb.mbName.containsIgnoreCase(q));
        }

        return builder;
    }
}
