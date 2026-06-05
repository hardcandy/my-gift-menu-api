package com.wx.gift.controller;

import com.wx.gift.dto.GiftProposalDTO;
import com.wx.gift.service.GiftProposalService;
import com.wx.gift.vo.GiftProposalVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/proposal")
public class GiftProposalController {
    @Autowired
    private GiftProposalService giftProposalService;

    @RequestMapping("/create")
    public GiftProposalDTO create(@RequestBody GiftProposalVo vo) { return giftProposalService.create(vo); }

    @RequestMapping("/list")
    public List<GiftProposalDTO> list(@RequestBody GiftProposalVo vo) { return giftProposalService.list(vo); }

    @RequestMapping("/detail")
    public GiftProposalDTO detail(@RequestBody GiftProposalVo vo) { return giftProposalService.detail(vo.getProposalId()); }

    @RequestMapping("/confirm")
    public GiftProposalDTO confirm(@RequestBody GiftProposalVo vo) { return giftProposalService.confirm(vo); }

    @RequestMapping("/reject")
    public GiftProposalDTO reject(@RequestBody GiftProposalVo vo) { return giftProposalService.reject(vo); }

    @RequestMapping("/cancel")
    public GiftProposalDTO cancel(@RequestBody GiftProposalVo vo) { return giftProposalService.cancel(vo); }

    @RequestMapping("/delete")
    public Boolean delete(@RequestBody GiftProposalVo vo) { return giftProposalService.delete(vo); }
}
