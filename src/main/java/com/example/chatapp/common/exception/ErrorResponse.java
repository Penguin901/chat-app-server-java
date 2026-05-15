package com.example.chatapp.common.exception;

import org.springframework.http.ResponseEntity;

public record ErrorResponse(
        String name,
        String message
) {
    // GlobalExceptionHandler에서 사용
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode e) {
        return ResponseEntity
                .status(e.getHttpStatus().value())
                .body(new ErrorResponse(
                        e.name(),
                        e.getMessage()
                ));
    }

    // 스프링시큐리티에서 사용
    public static ErrorResponse from(ErrorCode e) {
        return new ErrorResponse(
                e.name(),
                e.getMessage()
        );
    }
}

