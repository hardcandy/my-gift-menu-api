package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class StudyRecordVo {
    private String openId;
    private Integer familyId;
    private Integer recordId;
    private Integer childId;
    private Integer secondChildId;
    private String grade;
    private String subject;
    private Integer itemId;
    private Date recordDate;
    private String scoreType;
    private String scoreValue;
    private Integer hasError;
    private Integer errorCount;
    private Integer corrected;
    private String note;
    private String attachmentFileId;
    private List<Integer> childIds;
}
