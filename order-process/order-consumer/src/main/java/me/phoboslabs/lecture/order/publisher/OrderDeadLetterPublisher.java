package me.phoboslabs.lecture.order.publisher;

import me.phoboslabs.lecture.order.publisher.binder.DeadLetterBinder;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(DeadLetterBinder.class)
public class OrderDeadLetterPublisher {
}
