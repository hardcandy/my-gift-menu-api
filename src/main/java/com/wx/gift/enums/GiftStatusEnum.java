package com.wx.gift.enums;

public enum GiftStatusEnum {
    PENDING_REVIEW("pending_review", "等家人看"),
    REJECTED("rejected", "愿望先藏起来啦"),
    OPEN("open", "我的愿望在漂流"),
    CLAIMED("claimed", "有人偷偷接住啦"),
    CONFIRMED("confirmed", "心意正在飞奔而来"),
    RECEIVED("received", "收到啦"),
    FEEDBACK_PENDING("feedback_pending", "快来夸我"),
    FEEDBACK_DONE("feedback_done", "心愿圆满"),
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
