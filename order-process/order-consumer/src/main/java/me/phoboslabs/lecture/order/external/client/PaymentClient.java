package me.phoboslabs.lecture.order.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "payment")
public interface PaymentClient {

	@PostMapping("/payment")
	String payment(final long price);
}