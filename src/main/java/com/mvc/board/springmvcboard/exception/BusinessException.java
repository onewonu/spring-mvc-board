package com.mvc.board.springmvcboard.exception;

public abstract class BusinessException extends RuntimeException {

    BusinessException(String message) {
        super(message);
    }

    public abstract int getStatusCode();
}