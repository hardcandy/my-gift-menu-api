package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@TableName("t_gift_restaurant")
public class Restaurant {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer familyId;
    private String ownerOpenId;
    private String imageFileId;
    private String name;
    private String location;
    private BigDecimal averageCost;
    private BigDecimal distanceKm;
    private String recommendedDishes;
    private String cuisineType;
    private String tags;
    private String status;
    private BigDecimal averageScore;
    private Date lastAteAt;
    private Date createTime;
    private Date modifyTime;
}
