package com.naver.lecture.order.external.publish.binder;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OrderBinder {

    String MESSAGE_CHANNEL_NAME = "publishChannel";

    @Output(MESSAGE_CHANNEL_NAME)
    MessageChannel channel();
}
