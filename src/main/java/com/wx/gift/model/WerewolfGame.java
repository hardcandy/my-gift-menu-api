package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_werewolf_game")
public class WerewolfGame {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String roomCode;
    private String hostOpenId;
    private String hostName;
    private String playersText;
    private Integer playerCount;
    private String configText;
    private String rolesText;
    private String aliveText;
    private String actionsText;
    private String speechText;
    private String voteText;
    private String nightResultText;
    private Integer dayNo;
    private String phase;
    private String winnerCamp;
    private String resultText;
    private String status;
    private Date startedAt;
    private Date finishedAt;
    private Date createTime;
    private Date modifyTime;
}
