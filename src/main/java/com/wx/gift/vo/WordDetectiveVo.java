package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class WordDetectiveVo {
    private String openId;
    private Integer familyId;
    private Integer childId;
    private Integer packId;
    private Integer wordId;
    private String name;
    private String grade;
    private String semester;
    private String source;
    private String note;
    private String wordText;
    private String pinyin;
    private String hint;
    private String phrases;
    private String mistakeTip;
    private String unitName;
    private Integer needRead;
    private Integer needWrite;
    private Integer important;
    private String batchText;
    private String mode;
    private List<Map<String, Object>> answers;
    private Integer limit;
}
