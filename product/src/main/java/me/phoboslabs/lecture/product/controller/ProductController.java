package me.phoboslabs.lecture.product.controller;

import me.phoboslabs.lecture.adaptor.domain.EmptyJsonResponse;
import me.phoboslabs.lecture.adaptor.domain.product.ProductAdaptor;
import me.phoboslabs.lecture.adaptor.domain.product.param.ProductParam;
import me.phoboslabs.lecture.product.service.ProductService;
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
