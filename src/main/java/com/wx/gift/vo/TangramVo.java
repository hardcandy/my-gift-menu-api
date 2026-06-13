package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TangramVo {
    private String openId;
    private Integer familyId;
    private String levelId;
    private String levelName;
    private Integer durationMs;
    private Integer hintCount;
    private Integer moveCount;
    private Integer starCount;
    private Integer limit;
}
