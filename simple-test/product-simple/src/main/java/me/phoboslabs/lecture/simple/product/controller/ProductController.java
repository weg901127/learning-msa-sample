package me.phoboslabs.lecture.simple.product.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    @Value("${server.port}")
    private int port;

    @GetMapping
    public String checkInstance() {
        return "I'm Product " + this.port;
    }
}
