package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_word_progress")
public class WordProgress {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private Integer childId;
    private Integer wordId;
    private String recognizeStatus;
    private String readStatus;
    private String useStatus;
    private String writeStatus;
    private Integer recognizeCorrectStreak;
    private Integer readCorrectStreak;
    private Integer useCorrectStreak;
    private Integer writeCorrectStreak;
    private Integer recognizeWrongCount;
    private Integer readWrongCount;
    private Integer useWrongCount;
    private Integer writeWrongCount;
    private Date lastPracticedAt;
    private String status;
    private Date createTime;
    private Date modifyTime;
}
