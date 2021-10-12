package com.naver.lecture.order.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {

    WAITING_FOR_PAYMENT("waiting_for_payment"),
    ASYNC_ORDER_REQUEST_COMPLETE("async_order_request_complete"),
    COMPLETE("complete"),
    FAILED("failed");

    private final String orderStatus;
}
