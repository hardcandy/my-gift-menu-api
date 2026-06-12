package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_word_pack")
public class WordPack {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String ownerOpenId;
    private Integer childId;
    private String name;
    private String grade;
    private String semester;
    private String source;
    private String note;
    private String status;
    private Date createTime;
    private Date modifyTime;
}
