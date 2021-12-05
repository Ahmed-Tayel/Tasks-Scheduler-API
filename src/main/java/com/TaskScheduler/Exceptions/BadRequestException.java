package com.TaskScheduler.Exceptions;

import java.io.IOException;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}