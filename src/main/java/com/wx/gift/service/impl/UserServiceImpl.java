package com.wx.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.gift.dto.WxGetOpenIdResDTO;
import com.wx.gift.mapper.BaseUserMapper;
import com.wx.gift.model.BaseUser;
import com.wx.gift.service.UserService;
import com.wx.gift.service.WxService;
import com.wx.gift.util.ValidatorUtil;
import com.wx.gift.vo.LoginVo;
import com.wx.gift.vo.ProfileVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private BaseUserMapper baseUserMapper;
    @Autowired
    private WxService wxService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseUser login(LoginVo vo) {
        ValidatorUtil.checkNotBlank(vo.getCode(), "code 不能为空");
        WxGetOpenIdResDTO openIdRes = wxService.getOpenId(vo.getCode());
        ValidatorUtil.checkNotNull(openIdRes, "获取微信身份失败");
        ValidatorUtil.checkNotBlank(openIdRes.getOpenId(), "获取 openId 失败");

        BaseUser user = getByOpenId(openIdRes.getOpenId());
        Date now = new Date();
        if (user == null) {
            user = new BaseUser();
            user.setOpenId(openIdRes.getOpenId());
            user.setSessionKey(openIdRes.getSessionKey());
            user.setUnionId(openIdRes.getUnionId());
            user.setNickName(StringUtils.defaultIfBlank(vo.getNickName(), "微信用户"));
            user.setAvatarUrl(vo.getAvatarUrl());
            user.setRole(StringUtils.defaultIfBlank(vo.getRole(), "parent"));
            user.setCreateTime(now);
            user.setModifyTime(now);
            baseUserMapper.insert(user);
            return baseUserMapper.selectById(user.getId());
        }
        user.setSessionKey(openIdRes.getSessionKey());
        if (StringUtils.isNotBlank(vo.getNickName())) {
            user.setNickName(vo.getNickName());
        }
        if (StringUtils.isNotBlank(vo.getAvatarUrl())) {
            user.setAvatarUrl(vo.getAvatarUrl());
        }
        if (StringUtils.isNotBlank(vo.getRole())) {
            user.setRole(vo.getRole());
        }
        user.setModifyTime(now);
        baseUserMapper.updateById(user);
        return baseUserMapper.selectById(user.getId());
    }

    @Override
    public BaseUser getByOpenId(String openId) {
        if (StringUtils.isBlank(openId)) {
            return null;
        }
        return baseUserMapper.selectOne(new LambdaQueryWrapper<BaseUser>().eq(BaseUser::getOpenId, openId));
    }

    @Override
    public BaseUser updateProfile(ProfileVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        BaseUser user = getByOpenId(vo.getOpenId());
        ValidatorUtil.checkNotNull(user, "用户不存在");
        if (StringUtils.isNotBlank(vo.getNickName())) {
            user.setNickName(vo.getNickName());
        }
        if (StringUtils.isNotBlank(vo.getAvatarUrl())) {
            user.setAvatarUrl(vo.getAvatarUrl());
        }
        if (StringUtils.isNotBlank(vo.getRole())) {
            user.setRole(vo.getRole());
        }
        user.setModifyTime(new Date());
        baseUserMapper.updateById(user);
        return baseUserMapper.selectById(user.getId());
    }
}

