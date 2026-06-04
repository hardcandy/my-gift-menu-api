package com.wx.gift.enums;

public enum GiftStatusEnum {
    PENDING_REVIEW("pending_review", "待审核"),
    REJECTED("rejected", "审核未通过"),
    OPEN("open", "可认领"),
    CLAIMED("claimed", "已占位"),
    CONFIRMED("confirmed", "已确认"),
    RECEIVED("received", "已收到"),
    FEEDBACK_PENDING("feedback_pending", "待反馈"),
    FEEDBACK_DONE("feedback_done", "已反馈"),
    COMPLETED("completed", "已完成"),
    THANKED("thanked", "已感谢"),
    CANCELED("canceled", "已取消");

    private final String code;
    private final String text;

    GiftStatusEnum(String code, String text) {
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

