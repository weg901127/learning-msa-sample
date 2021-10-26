package me.phoboslabs.lecture.order.listener;

import me.phoboslabs.lecture.adaptor.domain.order.OrderProductAdaptor;
import me.phoboslabs.lecture.adaptor.message.order.OrderMessage;
import me.phoboslabs.lecture.order.listener.binder.OrderBinder;
import me.phoboslabs.lecture.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableBinding(OrderBinder.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderProcessListener {

    private final OrderService orderService;

    @StreamListener("subscribeChannel")
    public void handleDirect(final OrderMessage message) {
        log.info("");
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        log.info("@ 메시지 수신 성공 : {}", message.getProductId());
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        log.info("");

        final OrderProductAdaptor orderProductAdaptor = this.orderService.orderProcess(message.getProductId(), message.getStock());
        if (orderProductAdaptor.getId() != null) {
            log.info("");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@ 주문처리가 완료 되었습니다. productId: {}, stock: {}", message.getProductId(), message.getStock());
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("");
        } else {
            log.info("");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@ 주문처리를 실패 했습니다. : productId: {}, stock: {}", message.getProductId(), message.getStock());
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("");
        }
    }
}
