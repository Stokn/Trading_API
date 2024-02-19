package com.vulturi.trading.api.exceptions;

public enum ApiError {
    MISSING_VALUE(0, "MISSING VALUE", 404),
    NULL_VALUE(1, "NULL VALUE", 404),
    MALFORMED_JSON(2, "Malformed JSON", 400),
    USER_DOES_NOT_EXIST(3, "User does not exist", 404),

    ACCOUNT_DOES_NOT_EXIST(3, "Account does not exist", 404),

    CANNOT_FIND_ANY_WALLET_TRANSACTION(4, "Cannot find any wallet transaction", 409),

    CANNOT_FIND_TARGET_EXCHANGE(5, "Cannot find target exchange", 409),

    AUTH_FAILED(6, "Auth filed", 409),
    FORGET_PASSWORD_FAILED(7, "Forget password failed", 409),
    LOG_OUT_FAILED(8, "Log out failed", 409),
    INVALID_PASSWORD(9, "Invalid password", 409),

    CANNOT_CREATE_USER(10, "Cannot create user", 409),

    COGNITO_INVALID_PARAM(11, "Amazon Cognito service encounters an invalid parameter", 409),
    WITHDRAW_ADDRESS_ALREADY_EXIST(12, "Withdraw address already exist", 409),
    SUSPICIOUS_ADDRESS(13, "Withdraw address already exist", 409),
    INVALID_CREDENTIALS(14, "Invalid credentials",409),
    COIN_DOES_NOT_EXIST(15, "Coin does not exist", 409),
    COIN_ALREADY_EXIST(16, "Coin already exist", 409),
    NETWORK_IS_NOT_AVAILABLE(17, "Coin already exist", 409),
    NO_TRADING_PORTFOLIO_ACTIVATED(18, "No existing trading portfolio", 409),
    USER_CANNOT_TRADE(19, "User cannot trade on this account", 409),
    NOT_ENOUGH_COIN_FOR_TRADING(20, "You have no sufficient balance for trading", 409),
    CANNOT_TRADE_PAIR(21, "Cannot trade the pair of the tradingOrder", 409);

    private int code;
    private String message;
    private Integer httpCode;

    ApiError(int code, String message, Integer httpCode) {
        this.code = code;
        this.message = message;
        this.httpCode = httpCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpCode() {
        return httpCode != null ? httpCode : 500;
    }

}
