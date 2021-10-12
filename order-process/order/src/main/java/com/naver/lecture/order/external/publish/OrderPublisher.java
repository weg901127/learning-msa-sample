package com.naver.lecture.order.external.publish;

import com.naver.lecture.order.external.publish.binder.OrderBinder;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(OrderBinder.class)
public class OrderPublisher {
}
