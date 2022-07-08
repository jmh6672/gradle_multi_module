package org.example.advice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Failed Login")
public class FailedLoginException extends ErrorCommonException {
    public FailedLoginException(String message) {
        super(message);
    }
    public FailedLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
