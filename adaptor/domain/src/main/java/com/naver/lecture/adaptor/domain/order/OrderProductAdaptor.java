package com.naver.lecture.adaptor.domain.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderProductAdaptor {

    private Integer id;
    private Integer productId;
    private String productName;
    private Long productPrice;
    private Integer orderStock;
    private Long totalPrice;
    private String orderStatus;

    @Builder
    public OrderProductAdaptor(final Integer id, final Integer productId, final String productName
            , final Long productPrice, final Integer orderStock, final Long totalPrice, final String orderStatus) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.orderStock = orderStock;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }
}
