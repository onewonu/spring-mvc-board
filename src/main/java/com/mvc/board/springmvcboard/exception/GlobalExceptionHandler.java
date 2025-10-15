package com.mvc.board.springmvcboard.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_ATTRIBUTE = "error";

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model) {
        log.warn("BusinessException [{}]: {}", e.getStatusCode(), e.getMessage());
        model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());

        return switch (e.getStatusCode()) {
            case 404 -> "error/404";
            case 400 -> "error/400";
            default -> "error/500";
        };
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        log.error("Unexpected error occurred", e);
        model.addAttribute(ERROR_ATTRIBUTE, "An unexpected error occurred");
        return "error/500";
    }
}