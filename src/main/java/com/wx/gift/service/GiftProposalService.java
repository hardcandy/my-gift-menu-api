package com.wx.gift.service;

import com.wx.gift.dto.GiftProposalDTO;
import com.wx.gift.vo.GiftProposalVo;

import java.util.List;

public interface GiftProposalService {
    GiftProposalDTO create(GiftProposalVo vo);
    GiftProposalDTO detail(Integer id);
    List<GiftProposalDTO> list(GiftProposalVo vo);
    GiftProposalDTO confirm(GiftProposalVo vo);
    GiftProposalDTO reject(GiftProposalVo vo);
    GiftProposalDTO cancel(GiftProposalVo vo);
}

