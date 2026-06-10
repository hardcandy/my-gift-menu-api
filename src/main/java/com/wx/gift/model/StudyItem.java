package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_study_item")
public class StudyItem {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String ownerOpenId;
    private String name;
    private String subjectScope;
    private String gradeScope;
    private String scoreType;
    private String fieldConfig;
    private Integer correctionEnabled;
    private Integer sortOrder;
    private String status;
    private Date createTime;
    private Date modifyTime;
}
