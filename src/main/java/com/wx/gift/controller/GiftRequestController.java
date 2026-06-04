package com.wx.gift.controller;

import com.wx.gift.dto.GiftRequestDTO;
import com.wx.gift.service.GiftRequestService;
import com.wx.gift.vo.GiftActionVo;
import com.wx.gift.vo.GiftCreateVo;
import com.wx.gift.vo.GiftListVo;
import com.wx.gift.vo.GiftFeedbackVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gift")
public class GiftRequestController {
    @Autowired
    private GiftRequestService giftRequestService;

    @RequestMapping("/create")
    public GiftRequestDTO create(@RequestBody GiftCreateVo vo) {
        return giftRequestService.create(vo);
    }

    @RequestMapping("/list")
    public List<GiftRequestDTO> list(@RequestBody GiftListVo vo) {
        return giftRequestService.list(vo);
    }

    @RequestMapping("/detail")
    public GiftRequestDTO detail(@RequestBody GiftActionVo vo) {
        return giftRequestService.detail(vo.getGiftRequestId());
    }

    @RequestMapping("/approve")
    public GiftRequestDTO approve(@RequestBody GiftActionVo vo) {
        return giftRequestService.approve(vo);
    }

    @RequestMapping("/reject")
    public GiftRequestDTO reject(@RequestBody GiftActionVo vo) {
        return giftRequestService.reject(vo);
    }

    @RequestMapping("/claim")
    public GiftRequestDTO claim(@RequestBody GiftActionVo vo) {
        return giftRequestService.claim(vo);
    }

    @RequestMapping("/confirm")
    public GiftRequestDTO confirm(@RequestBody GiftActionVo vo) {
        return giftRequestService.confirm(vo);
    }

    @RequestMapping("/complete")
    public GiftRequestDTO complete(@RequestBody GiftActionVo vo) {
        return giftRequestService.complete(vo);
    }

    @RequestMapping("/thank")
    public GiftRequestDTO thank(@RequestBody GiftActionVo vo) {
        return giftRequestService.thank(vo);
    }

    @RequestMapping("/feedback")
    public GiftRequestDTO feedback(@RequestBody GiftFeedbackVo vo) {
        return giftRequestService.feedback(vo);
    }

    @RequestMapping("/cancel")
    public GiftRequestDTO cancel(@RequestBody GiftActionVo vo) {
        return giftRequestService.cancel(vo);
    }
}

