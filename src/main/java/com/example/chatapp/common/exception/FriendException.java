package com.example.chatapp.common.exception;

import lombok.Getter;

@Getter
public class FriendException extends RuntimeException {
    private final ErrorCode errorCode;

    public FriendException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}


