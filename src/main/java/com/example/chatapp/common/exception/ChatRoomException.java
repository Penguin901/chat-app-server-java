package com.example.chatapp.common.exception;

import lombok.Getter;

@Getter
public class ChatRoomException extends RuntimeException {
    private final ErrorCode errorCode;

    public ChatRoomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}


