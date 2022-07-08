package org.example.advice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends ErrorCommonException {
    public NotFoundException() { super(); }
    public NotFoundException(String msg) { super(msg); log.error(msg);  }
    public NotFoundException(Throwable t) { super(t); }
    public NotFoundException(String msg, Throwable t) {
        super(msg, t);

        log.error(msg);
    }
}
