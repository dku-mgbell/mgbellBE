package com.mgbell.review.repository;

import com.mgbell.review.model.dto.request.ReviewFilterRequest;
import com.mgbell.review.model.entity.Review;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mgbell.review.model.entity.QReview.review;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Page<Review> findByWhere(Pageable pageable, ReviewFilterRequest request, Long storeId) {
        List<Review> content =
                queryFactory
                        .select(review)
                        .from(review)
                        .where(
                                review.store.id.eq(storeId),
                                onlyPhotosEq(request.getOnlyPhotos())
                        )
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(getSort(pageable, review))
                        .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        review.store.id.eq(storeId),
                        onlyPhotosEq(request.getOnlyPhotos())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression onlyPhotosEq(Boolean image) {
        return image != null ? review.images.size().gt(0) : null;
    }

    public static <T> OrderSpecifier<?>[] getSort(Pageable pageable, EntityPathBase<T> qClass) {
        return pageable.getSort().stream().map(order ->
                        new OrderSpecifier(
                                Order.valueOf(order.getDirection().name()),
                                Expressions.path(Object.class, qClass, order.getProperty())
                        )).toList()
                .toArray(new OrderSpecifier[0]);
    }
}
