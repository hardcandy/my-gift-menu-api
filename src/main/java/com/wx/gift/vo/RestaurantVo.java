package com.wx.gift.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class RestaurantVo {
    private String openId;
    private Integer familyId;
    private Integer restaurantId;
    private String imageFileId;
    private String name;
    private String location;
    private BigDecimal averageCost;
    private BigDecimal distanceKm;
    private String recommendedDishes;
    private String cuisineType;
    private String tags;
    private String dishes;
    private String note;
    private List<ScoreVo> scores;

    @Getter
    @Setter
    public static class ScoreVo {
        private String scorerOpenId;
        private String scorerName;
        private BigDecimal score;
        private String note;
    }
}
