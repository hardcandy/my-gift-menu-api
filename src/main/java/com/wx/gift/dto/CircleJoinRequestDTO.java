package com.wx.gift.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CircleJoinRequestDTO {
    private Integer id;
    private Integer familyId;
    private String familyName;
    private String circleType;
    private String applicantOpenId;
    private String applicantNickName;
    private String requestedRole;
    private String status;
    private Date createTime;
}

