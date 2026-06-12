package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GomokuGameVo {
    private String openId;
    private Integer familyId;
    private Integer gameId;
    private String roomCode;
    private Integer row;
    private Integer col;
}
