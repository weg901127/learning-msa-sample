package com.naver.lecture.order.service;

import com.naver.lecture.adaptor.domain.exception.OrderException;
import com.naver.lecture.adaptor.domain.order.OrderProductAdaptor;
import com.naver.lecture.order.domain.enums.OrderStatus;
import com.naver.lecture.adaptor.domain.product.ProductAdaptor;
import com.naver.lecture.adaptor.domain.product.param.ProductParam;
import com.naver.lecture.adaptor.message.order.OrderMessage;
import com.naver.lecture.order.domain.OrderProduct;
import com.naver.lecture.order.domain.repository.OrderProductRepository;
import com.naver.lecture.order.external.client.PaymentClient;
import com.naver.lecture.order.external.client.ProductClient;
import com.naver.lecture.order.external.publish.binder.OrderBinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderService {

    private final ProductClient productClient;
    private final PaymentClient paymentClient;

    private final OrderBinder orderBinder;

    private final OrderProductRepository orderProductRepository;

    @Transactional
    public OrderProductAdaptor orderDirect(final int productId, final int stock) {
        final ResponseEntity<ProductAdaptor> product = this.productClient.getProduct(productId);
        if (product.getStatusCode() == HttpStatus.OK && product.getBody().productIsValid()) {
            final ProductAdaptor productAdaptor = product.getBody();
            if (productAdaptor.getStock() < stock) {
                log.error("주문가능한 재고수량이 부족합니다. productId: {}", productId);
                throw new OrderException("주문가능한 재고수량이 부족합니다.");
            }

            log.info("");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@ product service 통신 성공 - 상품 정보를 성공적으로 가지고 왔습니다.");
            log.info("@  - 상품명 : {}", productAdaptor.getName());
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("");

            // 상품 재고 차감
            final ProductParam productParam = new ProductParam(productId, stock);
            final ResponseEntity<ProductAdaptor> updatedStockProductEntity = this.productClient.updateStock(productParam);
            if (updatedStockProductEntity.getStatusCode() != HttpStatus.OK) {
                log.error("주문서 검증이 실패 했습니다. 상품정보가 없습니다. product api error,  productId: {}, stock: {}", productId, stock);
                throw new OrderException("주문서 검증이 실패 했습니다. 상품정보가 없습니다.");
            }

            final ProductAdaptor updatedStockProduct = updatedStockProductEntity.getBody();

            log.info("");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@ product service 통신 성공 - 상품 재고 성공적으로 차감 했습니다.");
            log.info("@  - 재고 : {}", updatedStockProduct.getStock());
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("");

            // 주문서 최초 생성
            final OrderProduct orderProduct = OrderProduct.create(
                                                updatedStockProduct.getId(),
                                                updatedStockProduct.getName(),
                                                updatedStockProduct.getPrice()
                                        );
            orderProduct.updateOrderPriceDetail(stock);

            final OrderProduct savedOrderProduct = this.orderProductRepository.save(orderProduct);
            if (savedOrderProduct.getId() == null) {
                log.error("주문서 생성이 실패 했습니다. 상품정보가 없습니다. productId: {}, stock: {}", productId, stock);
                throw new OrderException("주문서 생성이 실패 했습니다. 상품정보가 없습니다.");
            }

            log.info("");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@ 주문서 생성이 완료되었습니다.");
            log.info("@  - 상품명 : {}", savedOrderProduct.getProductName());
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("");

            // 결제모듈 연동
            final ResponseEntity<String> paymentResultMessage = this.paymentClient.payment(savedOrderProduct.getTotalPrice());
            if (paymentResultMessage.getStatusCode() == HttpStatus.OK
                    && "COMPLETE".equalsIgnoreCase(paymentResultMessage.getBody())) {
                savedOrderProduct.setStatus(OrderStatus.COMPLETE);
            } else {
                savedOrderProduct.setStatus(OrderStatus.FAILED);
                throw new OrderException("주문서 생성이 실패 했습니다. 결제가 실패했습니다.");
            }

            log.info("");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@ payment service 결제 연동이 완료되었습니다.");
            log.info("@  - 총 가격 : {}", savedOrderProduct.getTotalPrice());
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("");

            // 결제 성공 상태 업데이트
            final OrderProduct completeOrderProduct = this.orderProductRepository.save(savedOrderProduct);

            log.info("");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@ 주문 완료.");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("");

            return this.getOrderProductAdaptor(completeOrderProduct);
        } else {
            log.error("상품이 존재하지 않습니다. productId: {}, stock: {}", productId, stock);
            throw new OrderException("상품이 존재하지 않습니다.");
        }
    }

    public OrderProductAdaptor orderAsync(final int productId, final int stock) {
        final OrderMessage orderMessage = OrderMessage.builder().productId(productId).stock(stock).build();
        final boolean sendResult = this.orderBinder.channel().send(MessageBuilder.withPayload(orderMessage).build());
        if (sendResult == false) {
            log.error("메시지 전송이 실패했습니다. productId: {}, stock: {}", productId, stock);
            throw new OrderException("메시지 전송이 실패했습니다.");
        }
        final OrderProduct orderProduct = OrderProduct.createForAsync(productId, stock, OrderStatus.ASYNC_ORDER_REQUEST_COMPLETE);
        return this.getOrderProductAdaptor(orderProduct);
    }

    private OrderProductAdaptor getOrderProductAdaptor(final OrderProduct orderProduct) {
        return OrderProductAdaptor.builder()
                .id(orderProduct.getId())
                .orderStock(orderProduct.getOrderStock())
                .productId(orderProduct.getProductId())
                .productName(orderProduct.getProductName())
                .productPrice(orderProduct.getProductPrice())
                .totalPrice(orderProduct.getTotalPrice())
                .orderStatus(orderProduct.getStatus().name())
                .build();
    }
}
