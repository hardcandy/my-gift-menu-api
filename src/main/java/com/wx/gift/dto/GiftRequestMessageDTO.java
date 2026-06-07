package com.wx.gift.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GiftRequestMessageDTO {
    private Integer id;
    private Integer giftRequestId;
    private String openId;
    private String nickName;
    private String content;
    private Date createTime;
}
