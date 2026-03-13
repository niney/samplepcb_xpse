package kr.co.samplepcb.xpse.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.samplepcb.xpse.domain.entity.QSpBomDocument;
import kr.co.samplepcb.xpse.domain.entity.SpBomDocument;
import kr.co.samplepcb.xpse.pojo.SpBomDocumentSearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class SpBomDocumentRepositoryImpl implements SpBomDocumentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public SpBomDocumentRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<SpBomDocument> findBomDocumentList(Pageable pageable, String mbId, SpBomDocumentSearchParam searchParam) {
        QSpBomDocument doc = QSpBomDocument.spBomDocument;
        BooleanBuilder where = buildSearchCondition(mbId, searchParam, doc);

        return queryFactory
                .selectFrom(doc)
                .where(where)
                .orderBy(doc.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countBomDocumentList(String mbId, SpBomDocumentSearchParam searchParam) {
        QSpBomDocument doc = QSpBomDocument.spBomDocument;
        BooleanBuilder where = buildSearchCondition(mbId, searchParam, doc);

        Long count = queryFactory
                .select(doc.count())
                .from(doc)
                .where(where)
                .fetchOne();
        return count != null ? count : 0L;
    }

    private BooleanBuilder buildSearchCondition(String mbId, SpBomDocumentSearchParam searchParam, QSpBomDocument doc) {
        if (searchParam == null) {
            searchParam = new SpBomDocumentSearchParam();
        }
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(doc.mbId.eq(mbId));

        if (StringUtils.isNotBlank(searchParam.getContentHash())) {
            builder.and(doc.contentHash.eq(searchParam.getContentHash()));
        }

        if (StringUtils.isNotBlank(searchParam.getFileName())) {
            builder.and(doc.fileName.containsIgnoreCase(searchParam.getFileName()));
        }

        if (StringUtils.isNotBlank(searchParam.getType())) {
            String type = searchParam.getType().trim();
            if (type.startsWith(".")) {
                type = type.substring(1);
            }
            if (StringUtils.isNotBlank(type)) {
                builder.and(doc.fileInfo.isNotNull());
            }
        }

        String q = searchParam.getQ();
        if (StringUtils.isBlank(q)) {
            return builder;
        }

        String field = searchParam.getField();
        if (StringUtils.isBlank(field)) {
            builder.andAnyOf(
                    doc.fileName.containsIgnoreCase(q),
                    doc.contentHash.containsIgnoreCase(q)
            );
            return builder;
        }

        switch (field) {
            case "fileName" -> builder.and(doc.fileName.containsIgnoreCase(q));
            case "contentHash" -> builder.and(doc.contentHash.eq(q));
            case "type" -> {
                String type = q.trim();
                if (type.startsWith(".")) {
                    type = type.substring(1);
                }
                if (StringUtils.isNotBlank(type)) {
                    builder.and(doc.fileName.lower().endsWith("." + type.toLowerCase()));
                }
            }
        }

        return builder;
    }
}
