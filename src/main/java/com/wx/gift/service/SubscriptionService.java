package com.wx.gift.service;

import com.wx.gift.vo.SubscriptionVo;

public interface SubscriptionService {
    void save(SubscriptionVo vo);
    boolean canSend(String openId, String templateId, String scene);
}

