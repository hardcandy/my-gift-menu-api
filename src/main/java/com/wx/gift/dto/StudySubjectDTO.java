package com.wx.gift.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudySubjectDTO {
    private Integer id;
    private Integer familyId;
    private String ownerOpenId;
    private String name;
    private String gradeScope;
    private Integer sortOrder;
    private Date createTime;
    private Date modifyTime;
}
