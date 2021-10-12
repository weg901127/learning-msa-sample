package com.naver.lecture.adaptor.domain.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductAdaptor {

    private String productNo;
    private Integer id;
    private String name;
    private Long price;
    private Integer stock;
    private Boolean soldOut;

    @Setter
    private String errorMessage;

    @Builder
    public ProductAdaptor(final Integer id, final String productNo, final String name, final Long price
            , final Integer stock, final Boolean soldOut) {
        this.id = id;
        this.productNo = productNo;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.soldOut = soldOut;
    }

    public boolean productIsValid() {
        return this.soldOut == false && this.stock > 0;
    }
}
