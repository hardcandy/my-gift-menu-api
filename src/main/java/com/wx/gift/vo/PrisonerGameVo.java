package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrisonerGameVo {
    private String openId;
    private Integer familyId;
    private Integer gameId;
    private String roomCode;
    private String choice;
    private Integer limit;
    private Integer totalRounds;
    private Integer hostScore;
    private Integer guestScore;
    private String winnerType;
    private String aiName;
    private String roundHistory;
}
