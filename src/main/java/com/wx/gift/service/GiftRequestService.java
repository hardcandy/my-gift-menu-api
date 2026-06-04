package com.wx.gift.service;

import com.wx.gift.dto.GiftRequestDTO;
import com.wx.gift.model.GiftRequest;
import com.wx.gift.vo.GiftActionVo;
import com.wx.gift.vo.GiftCreateVo;
import com.wx.gift.vo.GiftListVo;
import com.wx.gift.vo.GiftFeedbackVo;

import java.util.List;

public interface GiftRequestService {
    GiftRequestDTO create(GiftCreateVo vo);
    GiftRequestDTO detail(Integer id);
    List<GiftRequestDTO> list(GiftListVo vo);
    GiftRequestDTO approve(GiftActionVo vo);
    GiftRequestDTO reject(GiftActionVo vo);
    GiftRequestDTO claim(GiftActionVo vo);
    GiftRequestDTO confirm(GiftActionVo vo);
    GiftRequestDTO complete(GiftActionVo vo);
    GiftRequestDTO thank(GiftActionVo vo);
    GiftRequestDTO feedback(GiftFeedbackVo vo);
    GiftRequestDTO cancel(GiftActionVo vo);
}

