package com.wx.gift.service;

import com.wx.gift.dto.WxGetOpenIdResDTO;

public interface WxService {
    WxGetOpenIdResDTO getOpenId(String code);
    boolean sendSubscribeMessage(String openId, String templateId, String page, String title, String remark);
}

