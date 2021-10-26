package me.phoboslabs.lecture.order.listener.binder;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface OrderBinder {

    String MESSAGE_CHANNEL_NAME = "subscribeChannel";

    @Input(MESSAGE_CHANNEL_NAME)
    SubscribableChannel channel();
}
