package com.wx.gift.service;

import com.wx.gift.model.BaseUser;
import com.wx.gift.vo.LoginVo;
import com.wx.gift.vo.ProfileVo;

public interface UserService {
    BaseUser login(LoginVo vo);
    BaseUser getByOpenId(String openId);
    BaseUser updateProfile(ProfileVo vo);
}

