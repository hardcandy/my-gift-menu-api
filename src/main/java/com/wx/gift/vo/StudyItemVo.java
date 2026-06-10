package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyItemVo {
    private String openId;
    private Integer familyId;
    private Integer itemId;
    private String name;
    private String subjectScope;
    private String gradeScope;
    private String scoreType;
    private String fieldConfig;
    private Integer correctionEnabled;
    private Integer sortOrder;
}
