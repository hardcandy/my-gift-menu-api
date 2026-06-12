package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_word_play_record")
public class WordPlayRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private Integer childId;
    private String childName;
    private Integer packId;
    private String packName;
    private String operatorOpenId;
    private String mode;
    private Integer totalCount;
    private Integer correctCount;
    private Integer wrongCount;
    private Integer writePendingCount;
    private String summaryJson;
    private String note;
    private String status;
    private Date playedAt;
    private Date createTime;
    private Date modifyTime;
}
