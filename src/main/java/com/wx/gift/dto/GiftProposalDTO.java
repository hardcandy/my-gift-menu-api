package com.wx.gift.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GiftProposalDTO {
    private Integer id;
    private Integer familyId;
    private String senderOpenId;
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
    private String status;
    private String statusText;
    private String confirmOpenId;
    private String confirmNote;
    private Date createTime;
    private Date modifyTime;
}

