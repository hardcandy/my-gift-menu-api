package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_nim_game")
public class NimGame {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String roomCode;
    private String roomType;
    private String hostOpenId;
    private String hostName;
    private String guestOpenId;
    private String guestName;
    private String currentTurnOpenId;
    private String winnerOpenId;
    private String winnerName;
    private Integer initialStones;
    private Integer minTake;
    private Integer maxTake;
    private Integer remainingStones;
    private Integer totalRounds;
    private String moveHistory;
    private String status;
    private Date startedAt;
    private Date finishedAt;
    private Date createTime;
    private Date modifyTime;
}
