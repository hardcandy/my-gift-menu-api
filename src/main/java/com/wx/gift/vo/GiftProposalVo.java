package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GiftProposalVo {
    private String openId;
    private Integer familyId;
    private Integer proposalId;
    private String senderName;
    private String receiverOpenId;
    private String receiverName;
    private String title;
    private String reason;
    private String sceneType;
    private String budget;
    private String productLink;
    private String giftOptions;
    private String selectedOptions;
    private String confirmNote;
    private String scope;
}

