package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_tangram_record")
public class TangramRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String playerOpenId;
    private String playerName;
    private String levelId;
    private String levelName;
    private Integer durationMs;
    private Integer hintCount;
    private Integer moveCount;
    private Integer starCount;
    private String status;
    private Date createTime;
    private Date modifyTime;
}
