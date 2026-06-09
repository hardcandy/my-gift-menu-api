package com.wx.gift.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_gift_restaurant_visit")
public class RestaurantVisit {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer restaurantId;
    private Integer familyId;
    private String operatorOpenId;
    private String memberOpenIds;
    private String memberNames;
    private String dishes;
    private String note;
    private Date ateAt;
    private Date createTime;
}
