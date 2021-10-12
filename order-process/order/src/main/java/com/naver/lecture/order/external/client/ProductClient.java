package com.naver.lecture.order.external.client;

import com.naver.lecture.adaptor.domain.product.ProductAdaptor;
import com.naver.lecture.adaptor.domain.product.param.ProductParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product")
public interface ProductClient {

	@GetMapping("/product/{productId}")
	ResponseEntity<ProductAdaptor> getProduct(@PathVariable("productId") final int productId);

	@PostMapping("/product")
	ResponseEntity<ProductAdaptor> updateStock(final ProductParam productParam);
}