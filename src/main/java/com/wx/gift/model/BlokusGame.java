package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_blokus_game")
public class BlokusGame {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String roomCode;
    private String hostOpenId;
    private String hostName;
    private Integer playerCount;
    private Integer boardSize;
    private String playersText;
    private String boardText;
    private String remainingText;
    private String scoresText;
    private Integer currentSeat;
    private Integer turnNo;
    private Integer turnTimeSeconds;
    private Integer consecutivePasses;
    private String status;
    private String resultText;
    private Date startedAt;
    private Date turnStartedAt;
    private Date finishedAt;
    private Date createTime;
    private Date modifyTime;
}
