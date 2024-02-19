package com.vulturi.trading.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class ApiResponse<T> {

    private Boolean success;
    private T data;
    private Integer errorCode;
    private String errorMessage;


    public static <T> ApiResponse<T> buildSuccessfulApiResult(T data) {
        ApiResponse<T> result = new ApiResponse<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static <T> ApiResponse<T> buildFailedApiResult(int errorCode, String errorMessage) {
        ApiResponse<T> result = new ApiResponse<>();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
}
