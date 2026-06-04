package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CircleJoinRequestVo {
    private String openId;
    private Integer familyId;
    private Integer requestId;
    private String inviteCode;
    private String requestedRole;
}

