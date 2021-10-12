package com.naver.lecture.product.service;

import com.naver.lecture.adaptor.domain.exception.ProductException;
import com.naver.lecture.adaptor.domain.product.ProductAdaptor;
import com.naver.lecture.product.domain.Product;
import com.naver.lecture.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductAdaptor getProduct(final int productId) {
        final Product product = this.getProductByProductId(productId);
        final boolean isSoldOut = product.isSoldOut();
        product.updateSoldOut(isSoldOut);

        final Product updatedProduct = this.updateProduct(product);
        return this.convertProductAdaptor(updatedProduct);
    }

    @Transactional
    public ProductAdaptor updateStock(final int productId, final int decreaseStock) throws Exception {
        final Product product = this.getProductByProductId(productId);
        product.updateStock(decreaseStock);
        final Product updatedProduct = this.updateProduct(product);
        return this.convertProductAdaptor(updatedProduct);
    }

    private Product getProductByProductId(final int productId) {
        return this.productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("해당 상품이 없습니다. productId: "+productId));
    }

    private Product updateProduct(final Product product) {
        return this.productRepository.save(product);
    }

    private ProductAdaptor convertProductAdaptor(final Product updatedProduct) {
        return ProductAdaptor.builder()
                .id(updatedProduct.getId())
                .productNo(updatedProduct.getProductNo())
                .name(updatedProduct.getName())
                .price(updatedProduct.getPrice())
                .soldOut(updatedProduct.getSoldOut())
                .stock(updatedProduct.getStock())
                .build();
    }
}
