package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GiftCreateVo {
    private String openId;
    private Integer familyId;
    private Integer childId;
    private String receiverOpenId;
    private String receiverName;
    private String targetOpenId;
    private String targetName;
    private String title;
    private String reason;
    private String sceneType;
    private String expectedDate;
    private String budget;
    private String productLink;
}

