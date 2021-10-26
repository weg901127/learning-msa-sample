package me.phoboslabs.lecture.adaptor.domain.product.param;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductParam {

    private int productId;
    private int stock;

    public ProductParam(final int productId, final int stock) {
        this.productId = productId;
        this.stock = stock;
    }
}
