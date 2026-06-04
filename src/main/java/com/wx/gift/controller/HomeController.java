package com.wx.gift.controller;

import com.wx.gift.model.BaseUser;
import com.wx.gift.service.UserService;
import com.wx.gift.vo.LoginVo;
import com.wx.gift.vo.ProfileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private UserService userService;

    @RequestMapping("/userInfoByCode")
    public BaseUser getUserInfo(@RequestBody LoginVo vo) {
        return userService.login(vo);
    }

    @RequestMapping("/profile")
    public BaseUser updateProfile(@RequestBody ProfileVo vo) {
        return userService.updateProfile(vo);
    }
}

