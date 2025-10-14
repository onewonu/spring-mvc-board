package com.mvc.board.springmvcboard.exception;

public class InvalidInputException extends BusinessException {

    private static final int STATUS_CODE = 400;

    public InvalidInputException(String message) {
        super(message);
    }

    public static InvalidInputException of(String fieldName, String reason) {
        return new InvalidInputException(
                String.format("Invalid input for %s: %s", fieldName, reason)
        );
    }

    @Override
    public int getStatusCode() {
        return STATUS_CODE;
    }
}
