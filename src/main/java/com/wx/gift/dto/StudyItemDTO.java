package com.wx.gift.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudyItemDTO {
    private Integer id;
    private Integer familyId;
    private String ownerOpenId;
    private String name;
    private String subjectScope;
    private String gradeScope;
    private String fieldConfig;
    private Integer correctionEnabled;
    private Integer sortOrder;
    private Date createTime;
    private Date modifyTime;
}
