package org.example.model.dto;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Common
    BAD_REQUEST(400, "COM40001", "Bad Request"),
    INVALID_TYPE_VALUE(400, "COM40002", "Invalid Type Value"),
    NO_SUCH_FIELD(400, "COM40003", "No such filed"),

    // Auth
    FAIL_LOGIN(400, "AUT40001", "Failed to Login"),
    NOT_FOUND_TOKEN(401, "AUT40101", "Not found Token"),
    EXPIRED_TOKEN(401, "AUT40102", "Expired Token"),
    UNAUTHORIZED(401, "AUT40103", "Unauthorized"),
    ACCESS_DENIED(403, "COM40301", "Access is Denied"),
    NOT_FOUND_REQUEST(404, "COM40401", "Not Found Request"),
    METHOD_NOT_ALLOWED(405, "COM40501", "Method Not Allowed"),

    INTERNAL_SERVER_ERROR(500, "COM50001", "Internal Server Error"),
    NOT_FOUND_DATA(500, "COM50002", "Not Found data"),
    DUPLICATED_DATA(500,"COM5003","Duplicated data"),
    NULL_POINTER_ERROR(500, "COM50099", "Null Pointer Error"),
    UNKNOWN_ERROR(500, "COM50099", "Unknown error has occurred"),

    DB_CONNECTION_ERROR(500, "SQL50000", "Database Connection Error"),
    DB_EXECUTE_ERROR(500, "SQL50001", "Database Execute Error"),
    DB_VALIDATOR_ERROR(500, "SQL50002", "Database Validator Error")
    ;


    private int status;
    private final String code;
    private final String title;
    private final String msg;

    ErrorCode(final int status, final String code, final String title) {
        this.status = status;
        this.code = code;
        this.title = title;
        this.msg = "";
    }

    ErrorCode(final int status, final String code, final String title, final String detail) {
        this.status = status;
        this.title = title;
        this.code = code;
        this.msg = detail;
    }
}
