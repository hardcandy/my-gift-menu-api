package com.wx.gift.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudyRecordDTO {
    private Integer id;
    private Integer familyId;
    private Integer childId;
    private String childName;
    private String grade;
    private String subject;
    private Integer itemId;
    private String itemName;
    private String contentTitle;
    private Date recordDate;
    private String scoreType;
    private String scoreValue;
    private Integer hasError;
    private Integer errorCount;
    private Integer corrected;
    private String correctionMark;
    private String note;
    private String attachmentFileId;
    private Date createTime;
    private Date modifyTime;
}
