package com.naver.lecture.order.service;

import com.naver.lecture.adaptor.domain.exception.OrderException;
import com.naver.lecture.adaptor.domain.order.OrderProductAdaptor;
import com.naver.lecture.adaptor.domain.product.ProductAdaptor;
import com.naver.lecture.adaptor.domain.product.param.ProductParam;
import com.naver.lecture.adaptor.message.order.OrderMessage;
import com.naver.lecture.order.domain.OrderProduct;
import com.naver.lecture.order.domain.enums.OrderStatus;
import com.naver.lecture.order.domain.repository.OrderProductRepository;
import com.naver.lecture.order.external.client.PaymentClient;
import com.naver.lecture.order.external.client.ProductClient;
import com.naver.lecture.order.publisher.binder.DeadLetterBinder;
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

    private final OrderProductRepository orderProductRepository;

    private final DeadLetterBinder deadLetterBinder;

    @Transactional
    public OrderProductAdaptor orderProcess(final int productId, final int stock) {
        final ResponseEntity<ProductAdaptor> productAdaptorEntity = this.productClient.getProduct(productId);
        if (productAdaptorEntity.getStatusCode() == HttpStatus.OK && productAdaptorEntity.getBody().productIsValid()) {
            final ProductAdaptor productAdaptor = productAdaptorEntity.getBody();
            if (productAdaptor.getStock() < stock) {
                final String errorMessage = "주문가능한 재고수량이 부족합니다.";
                log.error("{} productId: {}", errorMessage, productId);
                this.orderDeadLetterPublish(productId, stock, errorMessage);
                throw new OrderException(errorMessage);
            }

            log.info("");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@ product service 통신 성공 - 상품 정보를 성공적으로 가지고 왔습니다.");
            log.info("@  - 상품명 : {}", productAdaptor.getName());
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("");

            // 상품 재고 차감
            final ProductParam productParam = new ProductParam(productId, stock);
            final ResponseEntity<ProductAdaptor> updatedProductEntity = this.productClient.updateStock(productParam);
            if (updatedProductEntity.getStatusCode() != HttpStatus.OK) {
                log.error("주문서 생성이 실패 했습니다. 재고차감이 실패 했습니다. productId: {}, stock: {}", productId, stock);
                throw new OrderException("주문서 생성이 실패 했습니다. 재고차감이 실패 했습니다");
            }
            final ProductAdaptor updatedProductAdaptor = updatedProductEntity.getBody();

            log.info("");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@ product service 통신 성공 - 상품 재고 성공적으로 차감 했습니다.");
            log.info("@  - 재고 : {}", updatedProductAdaptor.getStock());
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("");

            // 주문서 최초 생성
            final OrderProduct orderProduct = OrderProduct.create(
                                                        updatedProductAdaptor.getId(),
                                                        updatedProductAdaptor.getName(),
                                                        updatedProductAdaptor.getPrice()
                                                );
            orderProduct.updateOrderPriceDetail(stock);

            final OrderProduct savedOrderProduct = this.orderProductRepository.save(orderProduct);
            if (savedOrderProduct.getId() == null) {
                final String errorMessage = "주문서 생성이 실패 했습니다. 상품정보가 없습니다.";
                log.error("{} productId: {}, stock: {}", errorMessage, productId, stock);
                this.orderDeadLetterPublish(productId, stock, errorMessage);
                throw new OrderException(errorMessage);
            }

            log.info("");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@ 주문서 생성이 완료되었습니다.");
            log.info("@  - 상품명 : {}", savedOrderProduct.getProductName());
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("");

            // 결제모듈 연동
            final String paymentResultMessage = this.paymentClient.payment(savedOrderProduct.getTotalPrice());
            if ("COMPLETE".equalsIgnoreCase(paymentResultMessage)) {
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
            final OrderProduct orderProductComplete = orderProductRepository.save(savedOrderProduct);

            log.info("");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("@ 주문 처리 완료.");
            log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.info("");

            return this.getOrderProductAdaptor(orderProductComplete);
        } else {
            final String errorMessage = "상품이 존재하지 않습니다.";
            log.error("{} productId: {}, stock: {}", errorMessage, productId, stock);
            this.orderDeadLetterPublish(productId, stock, errorMessage);
            throw new OrderException(errorMessage);
        }
    }

    private OrderProductAdaptor getOrderProductAdaptor(final OrderProduct orderProduct) {
        return OrderProductAdaptor.builder()
                .id(orderProduct.getId())
                .orderStock(orderProduct.getOrderStock())
                .productId(orderProduct.getProductId())
                .productName(orderProduct.getProductName())
                .productPrice(orderProduct.getProductPrice())
                .orderStatus(orderProduct.getStatus().name())
                .totalPrice(orderProduct.getTotalPrice())
                .build();
    }

    /**
     * 주문 실패한 주문서들은 따로 다른 Queue에 보관한다.
     *
     * @param productId
     * @param stock
     * @param errorMessage
     */
    private void orderDeadLetterPublish(final int productId, final int stock, final String errorMessage) {
        final OrderMessage orderMessage = OrderMessage.builder().productId(productId).stock(stock).build();
        orderMessage.setErrorMessage(errorMessage);
        orderMessage.setErrorRetryCount(1);

        final boolean sendResult = this.deadLetterBinder.channel().send(MessageBuilder.withPayload(orderMessage).build());
        if (sendResult == false) {
            log.error("DeadLetter Queue 전송이 실패하였습니다. productId: {}, stock: {}, errorMessage: {}, errorRetryCount: {}"
                    , productId, stock, errorMessage, orderMessage.getErrorRetryCount());
        }
    }
}
