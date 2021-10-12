package com.naver.lecture.adaptor.message.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class OrderMessage {

    private final int productId;
    private final int stock;
    @Setter
    private String errorMessage;
    @Setter
    private int errorRetryCount = 0;

    @Builder
    public OrderMessage(final int productId, final int stock) {
        this.productId = productId;
        this.stock = stock;
    }
}
