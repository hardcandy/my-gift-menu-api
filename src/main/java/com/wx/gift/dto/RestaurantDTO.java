package com.wx.gift.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class RestaurantDTO {
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
    private BigDecimal averageScore;
    private Date lastAteAt;
    private Integer yearlyVisitCount;
    private Integer scoreCount;
    private Date createTime;
    private Date modifyTime;
}
