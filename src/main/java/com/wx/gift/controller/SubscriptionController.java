package com.wx.gift.controller;

import com.wx.gift.service.SubscriptionService;
import com.wx.gift.vo.SubscriptionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;

    @RequestMapping("/save")
    public Boolean save(@RequestBody SubscriptionVo vo) {
        subscriptionService.save(vo);
        return true;
    }
}

