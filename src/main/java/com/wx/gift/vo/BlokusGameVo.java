package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlokusGameVo {
    private String openId;
    private Integer familyId;
    private Integer gameId;
    private String roomCode;
    private Integer playerCount;
    private String pieceId;
    private Integer x;
    private Integer y;
    private String cellsText;
    private Integer limit;
}
