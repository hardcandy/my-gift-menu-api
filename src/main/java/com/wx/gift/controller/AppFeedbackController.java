package com.wx.gift.controller;

import com.wx.gift.mapper.AppFeedbackMapper;
import com.wx.gift.model.AppFeedback;
import com.wx.gift.util.ValidatorUtil;
import com.wx.gift.vo.AppFeedbackVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/feedback")
public class AppFeedbackController {
    @Autowired
    private AppFeedbackMapper appFeedbackMapper;

    @RequestMapping("/submit")
    public Boolean submit(@RequestBody AppFeedbackVo vo) {
        ValidatorUtil.checkNotBlank(vo.getContent(), "请填写反馈内容");
        Date now = new Date();
        AppFeedback feedback = new AppFeedback();
        feedback.setOpenId(StringUtils.defaultString(vo.getOpenId()));
        feedback.setNickName(StringUtils.defaultIfBlank(vo.getNickName(), "热心用户"));
        feedback.setFeedbackType(StringUtils.defaultIfBlank(vo.getFeedbackType(), "suggestion"));
        feedback.setContent(vo.getContent());
        feedback.setContact(StringUtils.defaultString(vo.getContact()));
        feedback.setPagePath(StringUtils.defaultString(vo.getPagePath()));
        feedback.setStatus("new");
        feedback.setCreateTime(now);
        feedback.setModifyTime(now);
        appFeedbackMapper.insert(feedback);
        return true;
    }
}
