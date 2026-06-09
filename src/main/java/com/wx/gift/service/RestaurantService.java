package com.wx.gift.service;

import com.wx.gift.dto.RestaurantDTO;
import com.wx.gift.vo.RestaurantVo;

import java.util.List;

public interface RestaurantService {
    List<RestaurantDTO> list(RestaurantVo vo);
    RestaurantDTO detail(RestaurantVo vo);
    RestaurantDTO save(RestaurantVo vo);
    Boolean delete(RestaurantVo vo);
    RestaurantDTO visit(RestaurantVo vo);
}
