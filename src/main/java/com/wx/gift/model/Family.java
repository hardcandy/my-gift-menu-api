package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_family")
public class Family {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String familyName;
    private String circleType;
    private String ownerOpenId;
    private String ownerRole;
    private Date createTime;
    private Date modifyTime;
}

