package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_river_crossing_record")
public class RiverCrossingRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String playerOpenId;
    private String playerName;
    private Integer stepCount;
    private Integer failCount;
    private Integer hintCount;
    private Integer durationMs;
    private Integer starCount;
    private String status;
    private Date createTime;
    private Date modifyTime;
}
