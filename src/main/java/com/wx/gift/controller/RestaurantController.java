package com.wx.gift.controller;

import com.wx.gift.dto.RestaurantDTO;
import com.wx.gift.service.RestaurantService;
import com.wx.gift.vo.RestaurantVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

    @RequestMapping("/list")
    public List<RestaurantDTO> list(@RequestBody RestaurantVo vo) {
        return restaurantService.list(vo);
    }

    @RequestMapping("/detail")
    public RestaurantDTO detail(@RequestBody RestaurantVo vo) {
        return restaurantService.detail(vo);
    }

    @RequestMapping("/save")
    public RestaurantDTO save(@RequestBody RestaurantVo vo) {
        return restaurantService.save(vo);
    }

    @RequestMapping("/delete")
    public Boolean delete(@RequestBody RestaurantVo vo) {
        return restaurantService.delete(vo);
    }

    @RequestMapping("/visit")
    public RestaurantDTO visit(@RequestBody RestaurantVo vo) {
        return restaurantService.visit(vo);
    }
}
