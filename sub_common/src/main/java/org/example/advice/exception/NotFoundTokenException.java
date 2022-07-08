package org.example.advice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Not found token")
public class NotFoundTokenException extends IllegalArgumentException{
    public NotFoundTokenException() {
    }

    public NotFoundTokenException(String s) {
        super(s);
    }

    public NotFoundTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundTokenException(Throwable cause) {
        super(cause);
    }
}
