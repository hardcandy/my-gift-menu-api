package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_prisoner_game")
public class PrisonerGame {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String roomCode;
    private String roomType;
    private String hostOpenId;
    private String hostName;
    private String guestOpenId;
    private String guestName;
    private Integer currentRound;
    private Integer totalRounds;
    private Integer hostScore;
    private Integer guestScore;
    private String hostChoice;
    private String guestChoice;
    private String winnerOpenId;
    private String winnerName;
    private String resultType;
    private String roundHistory;
    private String status;
    private Date startedAt;
    private Date finishedAt;
    private Date createTime;
    private Date modifyTime;
}
