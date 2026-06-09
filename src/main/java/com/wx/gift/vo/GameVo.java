package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameVo {
    private String openId;
    private Integer familyId;
    private Integer gameId;
    private String imageFileId;
    private String name;
    private String location;
    private Integer durationMinutes;
    private List<String> playerOpenIds;
    private List<String> playerNames;
    private String source;
}
