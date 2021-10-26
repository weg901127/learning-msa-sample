package me.phoboslabs.lecture.order.external.publish;

import me.phoboslabs.lecture.order.external.publish.binder.OrderBinder;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(OrderBinder.class)
public class OrderPublisher {
}
