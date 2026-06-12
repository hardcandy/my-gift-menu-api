package com.wx.gift.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SchulteRecordDTO {
    private Integer id;
    private Integer familyId;
    private Integer childId;
    private String childName;
    private String childOpenId;
    private String playerType;
    private String playerOpenId;
    private String playerName;
    private String operatorOpenId;
    private String gameName;
    private String gameMode;
    private String difficulty;
    private Integer gridSize;
    private Integer totalNumbers;
    private Date startTime;
    private Date endTime;
    private Integer durationMs;
    private Integer pauseDurationMs;
    private Integer completed;
    private Integer completedCount;
    private Integer errorCount;
    private Integer averageIntervalMs;
    private String rating;
    private String note;
    private Date createTime;
    private Date modifyTime;
}
