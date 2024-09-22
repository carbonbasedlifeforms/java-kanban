package com.yandex.kanbanboard.exceptions;

public class SendResponseException extends RuntimeException {
    public SendResponseException(String message) {
        super(message);
    }
}
