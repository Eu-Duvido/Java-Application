package com.euduvido.euduvido_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String code;
    private String message;
    private Object errors;

    public static ErrorResponse of(HttpStatus httpStatus, String code, String message) {
        return new ErrorResponse(LocalDateTime.now(), httpStatus.value(), code, message, null);
    }

    public static ErrorResponse of(HttpStatus httpStatus, String code, String message, Object errors) {
        return new ErrorResponse(LocalDateTime.now(), httpStatus.value(), code, message, errors);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}
