package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GiftFeedbackVo {
    private String openId;
    private Integer giftRequestId;
    private String rating;
    private String message;
    private String preference;
    private String parentNote;
}

