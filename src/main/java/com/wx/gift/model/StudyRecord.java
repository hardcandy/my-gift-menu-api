package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_study_record")
public class StudyRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private Integer childId;
    private String childName;
    private String grade;
    private String subject;
    private Integer itemId;
    private String itemName;
    private Date recordDate;
    private String scoreType;
    private String scoreValue;
    private Integer hasError;
    private Integer errorCount;
    private Integer corrected;
    private String correctionMark;
    private String note;
    private String attachmentFileId;
    private String status;
    private String createdByOpenId;
    private Date createTime;
    private Date modifyTime;
}
