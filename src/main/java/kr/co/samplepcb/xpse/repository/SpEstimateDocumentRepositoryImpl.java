package kr.co.samplepcb.xpse.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.samplepcb.xpse.domain.entity.QSpEstimateDocument;
import kr.co.samplepcb.xpse.domain.entity.SpEstimateDocument;
import kr.co.samplepcb.xpse.pojo.SpEstimateSearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class SpEstimateDocumentRepositoryImpl implements SpEstimateDocumentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SpEstimateDocumentRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<SpEstimateDocument> findEstimateList(Pageable pageable, SpEstimateSearchParam searchParam) {
        QSpEstimateDocument doc = QSpEstimateDocument.spEstimateDocument;

        BooleanBuilder where = buildSearchCondition(searchParam, doc);

        return queryFactory
                .selectFrom(doc)
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
}
