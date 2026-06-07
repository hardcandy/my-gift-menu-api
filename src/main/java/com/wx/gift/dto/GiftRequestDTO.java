package com.wx.gift.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class GiftRequestDTO {
    private Integer id;
    private Integer familyId;
    private Integer childId;
    private String childName;
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
    private String statusText;
    private String createdByOpenId;
    private String reviewerOpenId;
    private String claimedByOpenId;
    private String claimedByName;
    private String claimNote;
    private String feedbackRating;
    private String feedbackMessage;
    private String feedbackPreference;
    private String feedbackParentNote;
    private List<GiftRequestMessageDTO> messages;
    private Date createTime;
    private Date modifyTime;
}
