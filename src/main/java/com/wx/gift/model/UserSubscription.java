package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_user_subscription")
public class UserSubscription {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String openId;
    private String templateId;
    private String scene;
    private String status;
    private Date createTime;
    private Date modifyTime;
}

