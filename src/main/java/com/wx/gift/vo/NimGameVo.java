package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NimGameVo {
    private String openId;
    private Integer familyId;
    private Integer gameId;
    private String roomCode;
    private Integer takeCount;
    private Integer limit;
    private String winnerType;
    private Integer initialStones;
    private Integer maxTake;
    private Integer totalRounds;
    private String moveHistory;
}
