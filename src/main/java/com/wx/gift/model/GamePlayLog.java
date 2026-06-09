package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_game_play_log")
public class GamePlayLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer gameId;
    private Integer familyId;
    private String operatorOpenId;
    private String playerOpenIds;
    private String playerNames;
    private String source;
    private Date playedAt;
    private Date createTime;
}
