package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_feedback")
public class GiftFeedback {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer giftRequestId;
    private String feedbackOpenId;
    private String rating;
    private String message;
    private String preference;
    private String parentNote;
    private Date createTime;
    private Date modifyTime;
}

