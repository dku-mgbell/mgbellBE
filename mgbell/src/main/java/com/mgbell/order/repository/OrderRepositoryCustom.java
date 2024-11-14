package com.mgbell.order.repository;

import com.mgbell.order.model.dto.request.OwnerOrderFilterRequest;
import com.mgbell.order.model.entity.OrderState;
import com.mgbell.post.model.dto.request.PostPreviewRequest;
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

import static com.mgbell.order.model.entity.QOrder.order;

@Repository
@Slf4j
@RequiredArgsConstructor
public class OrderRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Page<com.mgbell.order.model.entity.Order> findByWhere(Pageable pageable, OwnerOrderFilterRequest request, Long storeId) {
        List<com.mgbell.order.model.entity.Order> content =
                queryFactory
                        .select(order)
                        .from(order)
                        .where(
                                order.store.id.eq(storeId),
                                state(request.getState())
                        )
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(getSort(pageable, order))
                        .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .where(
                        order.store.id.eq(storeId),
                        state(request.getState())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression state(OrderState state) {
        return state != null ? order.state.eq(state) : null;
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
