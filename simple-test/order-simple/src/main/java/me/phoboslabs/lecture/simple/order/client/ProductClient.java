package me.phoboslabs.lecture.simple.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "product-simple")
public interface ProductClient {

    @GetMapping("/product")
    String checkInstance();
}
