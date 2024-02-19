package com.vulturi.trading.api.models.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class AuthResponse<T> {
    

    private T data;

    public static <T> AuthResponse<T> build(T data) {
        AuthResponse<T> result = new AuthResponse<>();
        result.setData(data);
        return result;
    }


}
