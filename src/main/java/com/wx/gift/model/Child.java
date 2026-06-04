package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_child")
public class Child {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String childOpenId;
    private String childName;
    private String birthday;
    private String guardianOpenId;
    private Date createTime;
    private Date modifyTime;
}

