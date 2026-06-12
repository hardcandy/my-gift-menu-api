package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchulteRecordVo {
    private String openId;
    private Integer familyId;
    private Integer recordId;
    private Integer childId;
    private String difficulty;
    private Integer gridSize;
    private Integer totalNumbers;
    private Long startTimestamp;
    private Long endTimestamp;
    private Integer durationMs;
    private Integer pauseDurationMs;
    private Integer completed;
    private Integer completedCount;
    private Integer errorCount;
    private Integer averageIntervalMs;
    private String rating;
    private String note;
    private Integer limit;
}
