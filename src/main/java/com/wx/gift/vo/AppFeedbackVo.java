package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppFeedbackVo {
    private String openId;
    private String nickName;
    private String feedbackType;
    private String content;
    private String contact;
    private String pagePath;
}
