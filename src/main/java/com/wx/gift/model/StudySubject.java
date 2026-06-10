package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_study_subject")
public class StudySubject {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String ownerOpenId;
    private String name;
    private String gradeScope;
    private Integer sortOrder;
    private String status;
    private Date createTime;
    private Date modifyTime;
}
