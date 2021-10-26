package me.phoboslabs.lecture.adaptor.domain.exception;

public class OrderException extends RuntimeException {

    public OrderException(final String errorMessage) {
        super(errorMessage);
    }
}
