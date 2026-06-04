package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_user")
public class BaseUser {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String openId;
    private String sessionKey;
    private String unionId;
    private String nickName;
    private String avatarUrl;
    private String role;
    private Date createTime;
    private Date modifyTime;
}

