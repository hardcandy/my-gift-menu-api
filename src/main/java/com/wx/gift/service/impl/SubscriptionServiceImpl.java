package com.wx.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.gift.mapper.UserSubscriptionMapper;
import com.wx.gift.model.UserSubscription;
import com.wx.gift.service.SubscriptionService;
import com.wx.gift.util.ValidatorUtil;
import com.wx.gift.vo.SubscriptionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Autowired
    private UserSubscriptionMapper userSubscriptionMapper;

    @Override
    public void save(SubscriptionVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getTemplateId(), "templateId 不能为空");
        String scene = StringUtils.defaultIfBlank(vo.getScene(), "wish_pool_new");
        UserSubscription subscription = userSubscriptionMapper.selectOne(new LambdaQueryWrapper<UserSubscription>()
                .eq(UserSubscription::getOpenId, vo.getOpenId())
                .eq(UserSubscription::getTemplateId, vo.getTemplateId())
                .eq(UserSubscription::getScene, scene)
                .last("limit 1"));
        Date now = new Date();
        if (subscription == null) {
            subscription = new UserSubscription();
            subscription.setOpenId(vo.getOpenId());
            subscription.setTemplateId(vo.getTemplateId());
            subscription.setScene(scene);
            subscription.setCreateTime(now);
        }
        subscription.setStatus(StringUtils.defaultIfBlank(vo.getStatus(), "accept"));
        subscription.setModifyTime(now);
        if (subscription.getId() == null) {
            userSubscriptionMapper.insert(subscription);
        } else {
            userSubscriptionMapper.updateById(subscription);
        }
    }

    @Override
    public boolean canSend(String openId, String templateId, String scene) {
        if (StringUtils.isBlank(openId) || StringUtils.isBlank(templateId)) {
            return false;
        }
        UserSubscription subscription = userSubscriptionMapper.selectOne(new LambdaQueryWrapper<UserSubscription>()
                .eq(UserSubscription::getOpenId, openId)
                .eq(UserSubscription::getTemplateId, templateId)
                .eq(UserSubscription::getScene, StringUtils.defaultIfBlank(scene, "wish_pool_new"))
                .eq(UserSubscription::getStatus, "accept")
                .last("limit 1"));
        return subscription != null;
    }
}

