package com.wx.gift.dto;

public class GiftRuntimeException extends RuntimeException {
    private final int code;

    public GiftRuntimeException(String message) {
        this(500, message);
    }

    public GiftRuntimeException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

