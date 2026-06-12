package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiverCrossingVo {
    private String openId;
    private Integer familyId;
    private Integer stepCount;
    private Integer failCount;
    private Integer hintCount;
    private Integer durationMs;
    private Integer starCount;
    private Integer limit;
}
