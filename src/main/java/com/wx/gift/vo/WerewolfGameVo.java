package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WerewolfGameVo {
    private String openId;
    private Integer familyId;
    private Integer gameId;
    private String roomCode;
    private String actionType;
    private String targetOpenId;
    private String speechText;
    private Integer limit;
}
