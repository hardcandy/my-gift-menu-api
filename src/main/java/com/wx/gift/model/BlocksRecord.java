package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_blocks_record")
public class BlocksRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String playerOpenId;
    private String playerName;
    private Integer score;
    private Integer lineCount;
    private Integer level;
    private Integer durationMs;
    private String mode;
    private String status;
    private Date createTime;
    private Date modifyTime;
}
