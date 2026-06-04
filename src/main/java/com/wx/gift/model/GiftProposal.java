package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_proposal")
public class GiftProposal {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String senderOpenId;
    private String senderName;
    private String receiverOpenId;
    private String receiverName;
    private String title;
    private String reason;
    private String sceneType;
    private String budget;
    private String productLink;
    private String giftOptions;
    private String selectedOptions;
    private String status;
    private String confirmOpenId;
    private String confirmNote;
    private Date confirmedAt;
    private Date createTime;
    private Date modifyTime;
}

