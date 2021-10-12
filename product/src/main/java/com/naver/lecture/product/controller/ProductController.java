package com.naver.lecture.product.controller;

import com.naver.lecture.adaptor.domain.EmptyJsonResponse;
import com.naver.lecture.adaptor.domain.product.ProductAdaptor;
import com.naver.lecture.adaptor.domain.product.param.ProductParam;
import com.naver.lecture.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductController {

	private final ProductService productService;

	@GetMapping("/{productId}")
	public ResponseEntity<ProductAdaptor> getProduct(@PathVariable("productId") final int productId) {
		try {
			return ResponseEntity.ok(this.productService.getProduct(productId));
		} catch (Exception ex) {
			return new ResponseEntity(new EmptyJsonResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping
	public ResponseEntity<ProductAdaptor> updateStock(@RequestBody final ProductParam productParam) {
		try {
			return ResponseEntity.ok(this.productService.updateStock(productParam.getProductId(), productParam.getStock()));
		} catch (Exception ex) {
			return new ResponseEntity(new EmptyJsonResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
