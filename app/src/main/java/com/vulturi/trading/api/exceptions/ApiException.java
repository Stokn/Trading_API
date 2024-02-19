package com.vulturi.trading.api.exceptions;

import lombok.*;



@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class ApiException extends Exception {
    private int code;
    private int httpCode;
    private String additionalInfo;

    public ApiException(ApiError apiError) {
        this.httpCode = apiError.getHttpCode();
        this.code = apiError.getCode();
        this.additionalInfo = apiError.getMessage();

    }

    public ApiException(ApiError apiError, String additionalInfo) {
        this.httpCode = apiError.getHttpCode();
        this.code = apiError.getCode();
        this.additionalInfo = additionalInfo;

    }
    public ApiException(int httpCode,int code, String additionalInfo) {
        this.httpCode = httpCode;
        this.code = code;
        this.additionalInfo = additionalInfo;

    }
    public ApiException(int httpCode, String additionalInfo) {
        this.httpCode = httpCode;
        this.additionalInfo = additionalInfo;
    }

}
