package com.naver.lecture.order.controller;

import com.naver.lecture.adaptor.domain.EmptyJsonResponse;
import com.naver.lecture.adaptor.domain.order.OrderProductAdaptor;
import com.naver.lecture.order.controller.param.OrderParam;
import com.naver.lecture.order.domain.OrderProduct;
import com.naver.lecture.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderController {

	private final OrderService orderService;

	@GetMapping
	public String getProductForTest() {
		return "test";
	}

	@PostMapping
	public ResponseEntity<OrderProductAdaptor> orderByDirect(@RequestBody final OrderParam orderParam) {
		try {
			return ResponseEntity.ok(this.orderService.orderDirect(orderParam.getProductId(), orderParam.getStock()));
		} catch (Exception ex) {
			return new ResponseEntity(new EmptyJsonResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/async")
	public ResponseEntity<OrderProductAdaptor> orderByAsync(@RequestBody final OrderParam orderParam) {
		try {
			return ResponseEntity.ok(this.orderService.orderAsync(orderParam.getProductId(), orderParam.getStock()));
		} catch (Exception ex) {
			return new ResponseEntity(new EmptyJsonResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
