package com.naver.lecture.order.publisher;

import com.naver.lecture.order.publisher.binder.DeadLetterBinder;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(DeadLetterBinder.class)
public class OrderDeadLetterPublisher {
}
