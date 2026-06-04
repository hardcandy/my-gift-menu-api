package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_audit_log")
public class AuditLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer giftRequestId;
    private String operatorOpenId;
    private String action;
    private String comment;
    private Date createTime;
}

