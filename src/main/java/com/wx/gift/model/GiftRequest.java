package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_request")
public class GiftRequest {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private Integer childId;
    private String receiverOpenId;
    private String receiverName;
    private String targetOpenId;
    private String targetName;
    private String title;
    private String reason;
    private String sceneType;
    private String expectedDate;
    private String budget;
    private String productLink;
    private String imageFileId;
    private String status;
    private String createdByOpenId;
    private String reviewerOpenId;
    private String claimedByOpenId;
    private String claimedByName;
    private String claimNote;
    private Date reviewedAt;
    private Date claimedAt;
    private Date confirmedAt;
    private Date completedAt;
    private Date thankYouSentAt;
    private Date createTime;
    private Date modifyTime;
}
