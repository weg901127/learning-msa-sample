package com.naver.lecture.adaptor.domain.exception;

public class OrderException extends RuntimeException {

    public OrderException(final String errorMessage) {
        super(errorMessage);
    }
}
