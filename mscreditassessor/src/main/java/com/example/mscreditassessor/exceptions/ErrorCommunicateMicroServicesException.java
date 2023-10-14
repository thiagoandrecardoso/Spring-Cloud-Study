package com.example.mscreditassessor.exceptions;

import lombok.Getter;

public class ErrorCommunicateMicroServicesException extends Exception {
    @Getter
    private Integer status;
    public ErrorCommunicateMicroServicesException(String message, Integer status) {
        super(message);
        this.status = status;
    }
}
