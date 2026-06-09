package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_game")
public class Game {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String ownerOpenId;
    private String imageFileId;
    private String name;
    private String location;
    private Integer durationMinutes;
    private String lastPlayedByOpenIds;
    private String lastPlayedByNames;
    private Date lastPlayedAt;
    private String status;
    private Date createTime;
    private Date modifyTime;
}
