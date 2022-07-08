package org.example.advice.exception;

import org.example.model.dto.ErrorCode;

public class InvalidValueException extends ErrorCommonException {
    public InvalidValueException(String value) {
        super(value, ErrorCode.INVALID_TYPE_VALUE);
    }

    public InvalidValueException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }
}
