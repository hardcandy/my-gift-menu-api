package com.wx.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.gift.dto.GiftProposalDTO;
import com.wx.gift.mapper.GiftProposalMapper;
import com.wx.gift.mapper.FamilyMapper;
import com.wx.gift.mapper.FamilyMemberMapper;
import com.wx.gift.model.GiftProposal;
import com.wx.gift.model.Family;
import com.wx.gift.model.FamilyMember;
import com.wx.gift.service.GiftProposalService;
import com.wx.gift.util.ValidatorUtil;
import com.wx.gift.vo.GiftProposalVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GiftProposalServiceImpl implements GiftProposalService {
    @Autowired
    private GiftProposalMapper giftProposalMapper;
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GiftProposalDTO create(GiftProposalVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getTitle(), "提案标题不能为空");
        ValidatorUtil.checkNotBlank(vo.getGiftOptions(), "请至少填写一个礼物选项");
        ValidatorUtil.checkArgument(isCircleMember(vo.getFamilyId(), vo.getOpenId()), "只有圈内成员可以发起送礼提案");
        Date now = new Date();
        GiftProposal proposal = new GiftProposal();
        proposal.setFamilyId(vo.getFamilyId());
        proposal.setSenderOpenId(vo.getOpenId());
        proposal.setSenderName(StringUtils.defaultIfBlank(vo.getSenderName(), "送礼人"));
        proposal.setReceiverOpenId(vo.getReceiverOpenId());
        proposal.setReceiverName(vo.getReceiverName());
        proposal.setTitle(vo.getTitle());
        proposal.setReason(vo.getReason());
        proposal.setSceneType(StringUtils.defaultIfBlank(vo.getSceneType(), "日常心意"));
        proposal.setBudget(vo.getBudget());
        proposal.setProductLink(vo.getProductLink());
        proposal.setGiftOptions(vo.getGiftOptions());
        proposal.setStatus("pending_confirm");
        proposal.setCreateTime(now);
        proposal.setModifyTime(now);
        giftProposalMapper.insert(proposal);
        return detail(proposal.getId());
    }

    @Override
    public GiftProposalDTO detail(Integer id) {
        ValidatorUtil.checkNotNull(id, "proposalId 不能为空");
        GiftProposal proposal = giftProposalMapper.selectById(id);
        ValidatorUtil.checkNotNull(proposal, "送礼提案不存在");
        return toDto(proposal);
    }

    @Override
    public List<GiftProposalDTO> list(GiftProposalVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        LambdaQueryWrapper<GiftProposal> wrapper = new LambdaQueryWrapper<>();
        if (vo.getFamilyId() != null) {
            wrapper.eq(GiftProposal::getFamilyId, vo.getFamilyId());
        }
        if ("sentByMe".equals(vo.getScope())) {
            wrapper.eq(GiftProposal::getSenderOpenId, vo.getOpenId());
        }
        if ("toMe".equals(vo.getScope())) {
            wrapper.eq(GiftProposal::getReceiverOpenId, vo.getOpenId());
        }
        wrapper.orderByDesc(GiftProposal::getId);
        return giftProposalMapper.selectList(wrapper).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public GiftProposalDTO confirm(GiftProposalVo vo) {
        return changeStatus(vo, "confirmed");
    }

    @Override
    public GiftProposalDTO reject(GiftProposalVo vo) {
        return changeStatus(vo, "rejected");
    }

    @Override
    public GiftProposalDTO cancel(GiftProposalVo vo) {
        return changeStatus(vo, "canceled");
    }

    @Transactional(rollbackFor = Exception.class)
    public GiftProposalDTO changeStatus(GiftProposalVo vo, String status) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getProposalId(), "proposalId 不能为空");
        GiftProposal proposal = giftProposalMapper.selectById(vo.getProposalId());
        ValidatorUtil.checkNotNull(proposal, "送礼提案不存在");
        if ("canceled".equals(status)) {
            ValidatorUtil.checkArgument(vo.getOpenId().equals(proposal.getSenderOpenId()), "只有发起人可以取消");
        } else {
            ValidatorUtil.checkArgument(isCircleMember(proposal.getFamilyId(), vo.getOpenId()), "只有圈内成员可以确认或拒绝送礼提案");
            ValidatorUtil.checkArgument(!vo.getOpenId().equals(proposal.getSenderOpenId()), "发起人不能自己确认送礼提案");
            if (StringUtils.isNotBlank(proposal.getReceiverOpenId())) {
                ValidatorUtil.checkArgument(vo.getOpenId().equals(proposal.getReceiverOpenId()), "只有收礼人可以确认或拒绝");
            }
        }
        ValidatorUtil.checkArgument("pending_confirm".equals(proposal.getStatus()), "当前状态不可操作");
        proposal.setStatus(status);
        proposal.setConfirmOpenId(vo.getOpenId());
        proposal.setConfirmNote(vo.getConfirmNote());
        proposal.setSelectedOptions(vo.getSelectedOptions());
        proposal.setConfirmedAt(new Date());
        proposal.setModifyTime(new Date());
        giftProposalMapper.updateById(proposal);
        return detail(proposal.getId());
    }

    private boolean isCircleMember(Integer familyId, String openId) {
        if (familyId == null || StringUtils.isBlank(openId)) {
            return false;
        }
        Family family = familyMapper.selectById(familyId);
        if (family == null) {
            return false;
        }
        if (openId.equals(family.getOwnerOpenId())) {
            return true;
        }
        return familyMemberMapper.selectOne(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, familyId)
                .eq(FamilyMember::getMemberOpenId, openId)
                .last("limit 1")) != null;
    }

    private GiftProposalDTO toDto(GiftProposal proposal) {
        GiftProposalDTO dto = new GiftProposalDTO();
        BeanUtils.copyProperties(proposal, dto);
        dto.setStatusText(statusText(proposal.getStatus()));
        return dto;
    }

    private String statusText(String status) {
        if ("pending_confirm".equals(status)) return "待确认";
        if ("confirmed".equals(status)) return "已确认";
        if ("rejected".equals(status)) return "已拒绝";
        if ("canceled".equals(status)) return "已取消";
        return status;
    }
}

