package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_circle_join_request")
public class CircleJoinRequest {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String inviteCode;
    private String applicantOpenId;
    private String applicantNickName;
    private String requestedRole;
    private String status;
    private String approveOpenId;
    private Date approveTime;
    private Date createTime;
    private Date modifyTime;
}

