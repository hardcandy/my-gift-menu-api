package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlocksVo {
    private String openId;
    private Integer familyId;
    private Integer score;
    private Integer lineCount;
    private Integer level;
    private Integer durationMs;
    private String mode;
    private Integer limit;
}
