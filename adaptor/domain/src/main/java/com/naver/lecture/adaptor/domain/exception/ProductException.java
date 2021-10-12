package com.naver.lecture.adaptor.domain.exception;

public class ProductException extends RuntimeException {

    public ProductException(final String errorMessage) {
        super(errorMessage);
    }
}
