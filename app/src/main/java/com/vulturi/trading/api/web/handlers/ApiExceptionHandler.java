package com.vulturi.trading.api.web.handlers;


import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ApiException.class})
    protected ResponseEntity<ApiResponse<?>> handleConflict(Exception ex, WebRequest request) {
        ApiException apiException = ((ApiException) ex);
        log.error("API request error {}", ex.getMessage());
        ApiResponse<Object> body = ApiResponse.buildFailedApiResult(apiException.getCode(), apiException.getMessage() + "-" + apiException.getAdditionalInfo());
        return ResponseEntity.status(apiException.getHttpCode()).body(body);
    }

}
