package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_word_item")
public class WordItem {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private Integer packId;
    private String wordText;
    private String pinyin;
    private String hint;
    private String phrases;
    private String mistakeTip;
    private String unitName;
    private Integer needRead;
    private Integer needWrite;
    private Integer important;
    private String source;
    private String status;
    private Date createTime;
    private Date modifyTime;
}
