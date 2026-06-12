package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_gomoku_game")
public class GomokuGame {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String roomCode;
    private String blackOpenId;
    private String blackName;
    private String whiteOpenId;
    private String whiteName;
    private String currentTurn;
    private String boardText;
    private Integer moveCount;
    private Integer lastMoveIndex;
    private String winnerOpenId;
    private String winnerName;
    private String status;
    private Date createTime;
    private Date modifyTime;
}
