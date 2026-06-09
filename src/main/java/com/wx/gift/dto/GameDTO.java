package com.wx.gift.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GameDTO {
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
    private Integer monthlyPlayCount;
    private Date createTime;
    private Date modifyTime;
}
