package org.example.advice.exception;

import org.example.model.dto.ErrorCode;

public class DuplicatedDataException extends RuntimeException {
    private ErrorCode code;

    public DuplicatedDataException() { super(); }
    public DuplicatedDataException(String msg) { super(msg); }
    public DuplicatedDataException(Throwable t) { super(t); }
    public DuplicatedDataException(String msg, Throwable t) {
        super(msg, t);
    }

    public DuplicatedDataException(String msg, ErrorCode code) {
        super(msg);
        this.code = code;
    }
    public DuplicatedDataException(ErrorCode code) {
        super(code.getMsg());
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return code;
    }
}