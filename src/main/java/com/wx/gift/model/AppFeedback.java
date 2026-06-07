package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_app_feedback")
public class AppFeedback {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String openId;
    private String nickName;
    private String feedbackType;
    private String content;
    private String contact;
    private String pagePath;
    private String status;
    private Date createTime;
    private Date modifyTime;
}
