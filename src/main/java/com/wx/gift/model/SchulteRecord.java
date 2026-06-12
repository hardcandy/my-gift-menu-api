package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_schulte_record")
public class SchulteRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private Integer childId;
    private String childName;
    private String childOpenId;
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
    private String status;
    private Date createTime;
    private Date modifyTime;
}
