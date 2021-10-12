package com.naver.lecture.simple.order.controller;

import com.naver.lecture.simple.order.client.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderController {

    private final ProductClient productClient;

    @GetMapping
    public String checkInstance() {
        final String productResponse = this.productClient.checkInstance();
        return "I'm Order. I requested Product. Response is \""+productResponse+"\"";
    }
}
