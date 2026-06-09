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
@TableName("t_gift_restaurant_score")
public class RestaurantScore {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer visitId;
    private Integer restaurantId;
    private Integer familyId;
    private String scorerOpenId;
    private String scorerName;
    private BigDecimal score;
    private String note;
    private Date createTime;
}
