package org.example.advice.exception;

public class InterServerErrorException extends ErrorCommonException {
    public InterServerErrorException() { super(); }
    public InterServerErrorException(String msg) { super(msg); }
    public InterServerErrorException(Throwable t) { super(t); }
    public InterServerErrorException(String msg, Throwable t) {
        super(msg, t);
    }
}