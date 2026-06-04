package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_family_member")
public class FamilyMember {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String memberOpenId;
    private String memberRole;
    private Date createTime;
    private Date modifyTime;
}

