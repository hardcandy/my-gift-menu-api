package com.wx.gift.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CircleInviteDTO {
    private Integer familyId;
    private String familyName;
    private String circleType;
    private String inviteCode;
    private Date expireTime;
}

