package me.phoboslabs.lecture.order.external.client;

import me.phoboslabs.lecture.adaptor.domain.product.ProductAdaptor;
import me.phoboslabs.lecture.adaptor.domain.product.param.ProductParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "product")
public interface ProductClient {

	@GetMapping("/product/{productId}")
	ResponseEntity<ProductAdaptor> getProduct(@PathVariable("productId") final int productId);

	@PostMapping("/product")
	ResponseEntity<ProductAdaptor> updateStock(final ProductParam productParam);
}