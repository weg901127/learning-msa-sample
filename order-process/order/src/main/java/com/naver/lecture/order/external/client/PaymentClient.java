package com.naver.lecture.order.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment")
public interface PaymentClient {

	@PostMapping("/payment")
	ResponseEntity<String> payment(final long price);
}