package com.naver.lecture.order.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.naver.lecture.adaptor.domain.entity.BaseEntity;
import com.naver.lecture.order.domain.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    private Integer productId;
    private String productName;
    private Long productPrice;
    @Setter
    private Integer orderStock;
    private Long totalPrice;

    @Enumerated(EnumType.STRING) @Setter
    private OrderStatus status = OrderStatus.WAITING_FOR_PAYMENT;

    @Builder
    private OrderProduct(final int productId, final String productName, final long productPrice, final int orderStock, final OrderStatus status) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.orderStock = orderStock;
        this.status = status;
    }

    public static OrderProduct create(final int productId, final String productName, final long productPrice) {
        return OrderProduct.builder()
                .productId(productId)
                .productName(productName)
                .productPrice(productPrice)
                .build();
    }

    public static OrderProduct createForAsync(final int productId, final int orderStock, final OrderStatus orderStatus) {
        return OrderProduct.builder()
                .productId(productId)
                .orderStock(orderStock)
                .status(orderStatus)
                .build();
    }

    public void updateOrderPriceDetail(final int stock) {
        this.orderStock = stock;
        this.totalPrice = this.productPrice * this.orderStock;
    }
}
