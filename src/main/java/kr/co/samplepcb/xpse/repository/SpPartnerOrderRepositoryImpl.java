package kr.co.samplepcb.xpse.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.samplepcb.xpse.domain.entity.QG5Member;
import kr.co.samplepcb.xpse.domain.entity.QG5ShopItem;
import kr.co.samplepcb.xpse.domain.entity.QSpPartnerOrder;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderDetailDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderListDTO;
import kr.co.samplepcb.xpse.pojo.SpPartnerOrderSearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class SpPartnerOrderRepositoryImpl implements SpPartnerOrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SpPartnerOrderRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<SpPartnerOrderListDTO> findPartnerOrderList(Pageable pageable, SpPartnerOrderSearchParam searchParam) {
        QSpPartnerOrder po = QSpPartnerOrder.spPartnerOrder;
        QG5ShopItem item = QG5ShopItem.g5ShopItem;
        QG5Member member = QG5Member.g5Member;

        BooleanBuilder where = buildSearchCondition(searchParam, po);

        return queryFactory
                .select(Projections.constructor(SpPartnerOrderListDTO.class,
                        po.id, po.itId, po.partnerMbNo,
                        po.status, po.isSelectPartner, po.price,
                        po.forwarder, po.shipping, po.tracking,
                        po.estimateFile1Subj, po.estimateFile1,
                        po.memo, po.writeDate, po.modifyDate,
                        item.itName, item.itMaker, item.itModel,
                        item.itBrand, item.itImg1,
                        member.mbId, member.mbName, member.mbNick,
                        member.mbEmail, member.mbHp))
                .from(po)
                .leftJoin(po.shopItem, item)
                .leftJoin(po.partner, member)
                .where(where)
                .orderBy(po.writeDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countPartnerOrderList(SpPartnerOrderSearchParam searchParam) {
        QSpPartnerOrder po = QSpPartnerOrder.spPartnerOrder;

        BooleanBuilder where = buildSearchCondition(searchParam, po);

        Long count = queryFactory
                .select(po.count())
                .from(po)
                .where(where)
                .fetchOne();
        return count != null ? count : 0L;
    }

    @Override
    public SpPartnerOrderDetailDTO findPartnerOrderDetail(String itId, int partnerMbNo) {
        QSpPartnerOrder po = QSpPartnerOrder.spPartnerOrder;
        QG5ShopItem item = QG5ShopItem.g5ShopItem;
        QG5Member member = QG5Member.g5Member;

        return queryFactory
                .select(Projections.constructor(SpPartnerOrderDetailDTO.class,
                        po.id, po.itId, po.partnerMbNo, po.metaItem,
                        po.status, po.isSelectPartner, po.price,
                        po.forwarder, po.shipping, po.tracking,
                        po.estimateFile1Subj, po.estimateFile1,
                        po.memo, po.writeDate, po.modifyDate,
                        item.itName, item.itMaker, item.itModel,
                        item.itBrand, item.itImg1, item.itBasic,
                        item.itExplan,
                        member.mbId, member.mbName, member.mbNick,
                        member.mbEmail, member.mbHp))
                .from(po)
                .leftJoin(po.shopItem, item)
                .leftJoin(po.partner, member)
                .where(po.itId.eq(itId), po.partnerMbNo.eq(partnerMbNo))
                .fetchOne();
    }

    private BooleanBuilder buildSearchCondition(SpPartnerOrderSearchParam searchParam, QSpPartnerOrder po) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(searchParam.getItId())) {
            builder.and(po.itId.eq(searchParam.getItId()));
        }
        if (searchParam.getPartnerMbNo() != null) {
            builder.and(po.partnerMbNo.eq(searchParam.getPartnerMbNo()));
        }
        if (StringUtils.isNotBlank(searchParam.getStatus())) {
            builder.and(po.status.eq(searchParam.getStatus()));
        }

        builder.and(po.shopItem.isNotNull());

        return builder;
    }
}
