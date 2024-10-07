package com.mgbell.post.repository;

import com.mgbell.post.model.dto.request.PostPreviewRequest;
import com.mgbell.post.model.entity.Post;
import com.mgbell.store.model.entity.StoreType;
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

import static com.mgbell.post.model.entity.QPost.post;
import static com.mgbell.store.model.entity.QStore.store;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Page<Post> findByWhere(Pageable pageable, PostPreviewRequest request) {
        List<Post> content =
                queryFactory
                        .select(post)
                        .from(post)
                        .where(
                                onSaleEq(request.getOnSale()),
                                storeEq(request.getStoreType())
                        )
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(getSort(pageable, post))
                        .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(onSaleEq(request.getOnSale()),
                        storeEq(request.getStoreType()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression onSaleEq(Boolean onSale) {
        return onSale != null ? post.onSale.eq(onSale) : null;
    }

    private BooleanExpression storeEq(StoreType storeType) {
        return storeType != null ? store.storeType.eq(storeType) : null;
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
