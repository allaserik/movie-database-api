
package com.moviedb_api.exception;

import java.time.LocalDate;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionResponse {
    private int status;
    private String message;
    private String errorCode;
    private String errorCause;
    private String timestamp;
    private Map<String, String> fieldErrors;

    public ExceptionResponse(int status, String message, String errorCode) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDate.now().toString(); // ISO 8601 format
    }

    public ExceptionResponse(int status, String message, String errorCode, String errorCause) {
        this(status, message, errorCode);
        this.errorCause = errorCause;
    }



}
