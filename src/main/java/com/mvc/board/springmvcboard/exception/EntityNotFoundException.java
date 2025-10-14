package com.mvc.board.springmvcboard.exception;

public class EntityNotFoundException extends BusinessException {

    private static final int STATUS_CODE = 404;

    EntityNotFoundException(String message) {
        super(message);
    }

    public static EntityNotFoundException of(String entityName, Long id) {
        return new EntityNotFoundException(
                String.format("%s not found with id: %d", entityName, id)
        );
    }

    @Override
    public int getStatusCode() {
        return STATUS_CODE;
    }
}
