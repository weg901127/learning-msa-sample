package me.phoboslabs.lecture.order.publisher.binder;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface DeadLetterBinder {

    String MESSAGE_CHANNEL_NAME = "deadLetterChannel";

    @Output(MESSAGE_CHANNEL_NAME)
    MessageChannel channel();
}
