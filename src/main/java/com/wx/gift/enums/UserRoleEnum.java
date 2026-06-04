package com.wx.gift.enums;

public enum UserRoleEnum {
    PARENT("parent", "家长"),
    CHILD("child", "孩子"),
    RELATIVE("relative", "亲友");

    private final String code;
    private final String text;

    UserRoleEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}

