package com.wx.gift.enums;

public enum GiftStatusEnum {
    PENDING_REVIEW("pending_review", "等家人看"),
    REJECTED("rejected", "暂未通过"),
    OPEN("open", "可准备"),
    CLAIMED("claimed", "有人准备"),
    CONFIRMED("confirmed", "准备中"),
    RECEIVED("received", "已收到"),
    FEEDBACK_PENDING("feedback_pending", "等反馈"),
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
