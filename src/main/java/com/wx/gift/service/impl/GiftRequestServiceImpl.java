package com.wx.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.gift.dto.GiftRequestDTO;
import com.wx.gift.enums.GiftStatusEnum;
import com.wx.gift.mapper.AuditLogMapper;
import com.wx.gift.mapper.ChildMapper;
import com.wx.gift.mapper.GiftRequestMapper;
import com.wx.gift.mapper.GiftFeedbackMapper;
import com.wx.gift.mapper.FamilyMapper;
import com.wx.gift.mapper.BaseUserMapper;
import com.wx.gift.mapper.FamilyMemberMapper;
import com.wx.gift.model.AuditLog;
import com.wx.gift.model.Child;
import com.wx.gift.model.GiftRequest;
import com.wx.gift.model.GiftFeedback;
import com.wx.gift.model.Family;
import com.wx.gift.model.BaseUser;
import com.wx.gift.model.FamilyMember;
import com.wx.gift.service.GiftRequestService;
import com.wx.gift.util.ValidatorUtil;
import com.wx.gift.vo.GiftActionVo;
import com.wx.gift.vo.GiftCreateVo;
import com.wx.gift.vo.GiftListVo;
import com.wx.gift.vo.GiftFeedbackVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GiftRequestServiceImpl implements GiftRequestService {
    @Autowired
    private GiftRequestMapper giftRequestMapper;
    @Autowired
    private ChildMapper childMapper;
    @Autowired
    private AuditLogMapper auditLogMapper;
    @Autowired
    private GiftFeedbackMapper giftFeedbackMapper;
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private FamilyMemberMapper familyMemberMapper;
    @Autowired
    private BaseUserMapper baseUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GiftRequestDTO create(GiftCreateVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getTitle(), "礼物名称不能为空");

        Date now = new Date();
        GiftRequest request = new GiftRequest();
        request.setFamilyId(vo.getFamilyId());
        request.setChildId(vo.getChildId());
        request.setReceiverOpenId(vo.getReceiverOpenId());
        request.setReceiverName(vo.getReceiverName());
        request.setTargetOpenId(vo.getTargetOpenId());
        request.setTargetName(vo.getTargetName());
        request.setTitle(vo.getTitle());
        request.setReason(vo.getReason());
        request.setSceneType(StringUtils.defaultIfBlank(vo.getSceneType(), "日常心愿"));
        request.setExpectedDate(vo.getExpectedDate());
        request.setBudget(vo.getBudget());
        request.setProductLink(vo.getProductLink());
        request.setImageFileId(vo.getImageFileId());
        Family family = familyMapper.selectById(vo.getFamilyId());
        ValidatorUtil.checkNotNull(family, "圈子不存在");
        ValidatorUtil.checkArgument(isCircleMember(family.getId(), vo.getOpenId()), "只有圈内成员可以发布愿望");
        request.setStatus(GiftStatusEnum.OPEN.getCode());
        request.setCreatedByOpenId(vo.getOpenId());
        request.setCreateTime(now);
        request.setModifyTime(now);
        giftRequestMapper.insert(request);
        log(request.getId(), vo.getOpenId(), "create", "创建愿望");
        return detail(request.getId());
    }

    @Override
    public GiftRequestDTO detail(Integer id) {
        ValidatorUtil.checkNotNull(id, "giftRequestId 不能为空");
        GiftRequest request = giftRequestMapper.selectById(id);
        ValidatorUtil.checkNotNull(request, "愿望不存在");
        return toDto(request, childNameMap(java.util.Collections.singletonList(request)));
    }

    @Override
    public List<GiftRequestDTO> list(GiftListVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        LambdaQueryWrapper<GiftRequest> wrapper = new LambdaQueryWrapper<>();
        if (vo.getFamilyId() != null) {
            wrapper.eq(GiftRequest::getFamilyId, vo.getFamilyId());
        }
        if (StringUtils.isNotBlank(vo.getStatus())) {
            wrapper.eq(GiftRequest::getStatus, vo.getStatus());
        }
        if ("claimedByMe".equals(vo.getScope())) {
            wrapper.eq(GiftRequest::getClaimedByOpenId, vo.getOpenId());
        }
        wrapper.orderByDesc(GiftRequest::getId);
        List<GiftRequest> list = giftRequestMapper.selectList(wrapper);
        Map<Integer, String> childMap = childNameMap(list);
        return list.stream().map(item -> toDto(item, childMap)).collect(Collectors.toList());
    }

    @Override
    public GiftRequestDTO approve(GiftActionVo vo) {
        requireFamilyParent(vo.getGiftRequestId(), vo.getOpenId());
        return changeStatus(vo, GiftStatusEnum.PENDING_REVIEW.getCode(), GiftStatusEnum.OPEN.getCode(), "approve");
    }

    @Override
    public GiftRequestDTO reject(GiftActionVo vo) {
        requireFamilyParent(vo.getGiftRequestId(), vo.getOpenId());
        return changeStatus(vo, GiftStatusEnum.PENDING_REVIEW.getCode(), GiftStatusEnum.REJECTED.getCode(), "reject");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GiftRequestDTO claim(GiftActionVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        GiftRequest request = require(vo.getGiftRequestId());
        ValidatorUtil.checkArgument(isCircleMember(request.getFamilyId(), vo.getOpenId()), "只有圈内成员可以准备这份心意");
        ValidatorUtil.checkArgument(GiftStatusEnum.OPEN.getCode().equals(request.getStatus()), "这份心意现在还不能准备");
        ValidatorUtil.checkArgument(!vo.getOpenId().equals(request.getCreatedByOpenId()) && !vo.getOpenId().equals(request.getReceiverOpenId()), "不能准备自己的愿望");
        request.setStatus(GiftStatusEnum.CLAIMED.getCode());
        request.setClaimedByOpenId(vo.getOpenId());
        request.setClaimedByName(StringUtils.defaultIfBlank(vo.getComment(), "亲友"));
        request.setClaimNote(vo.getClaimNote());
        request.setClaimedAt(new Date());
        request.setModifyTime(new Date());
        giftRequestMapper.updateById(request);
        log(request.getId(), vo.getOpenId(), "claim", vo.getClaimNote());
        return detail(request.getId());
    }

    @Override
    public GiftRequestDTO confirm(GiftActionVo vo) {
        requireClaimConfirmOperator(vo.getGiftRequestId(), vo.getOpenId());
        return changeStatus(vo, GiftStatusEnum.CLAIMED.getCode(), GiftStatusEnum.CONFIRMED.getCode(), "confirm");
    }

    @Override
    public GiftRequestDTO complete(GiftActionVo vo) {
        requireClaimConfirmOperator(vo.getGiftRequestId(), vo.getOpenId());
        return changeStatus(vo, GiftStatusEnum.CONFIRMED.getCode(), GiftStatusEnum.FEEDBACK_PENDING.getCode(), "received");
    }

    @Override
    public GiftRequestDTO thank(GiftActionVo vo) {
        return changeStatus(vo, GiftStatusEnum.FEEDBACK_PENDING.getCode(), GiftStatusEnum.FEEDBACK_DONE.getCode(), "feedback_done");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GiftRequestDTO feedback(GiftFeedbackVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getGiftRequestId(), "giftRequestId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getRating(), "请选择喜欢程度");
        GiftRequest request = require(vo.getGiftRequestId());
        ValidatorUtil.checkArgument(canFeedback(request, vo.getOpenId()), "只有收礼人或家长可以填写反馈");
        ValidatorUtil.checkArgument(GiftStatusEnum.FEEDBACK_PENDING.getCode().equals(request.getStatus()), "当前状态不允许反馈");
        Date now = new Date();
        GiftFeedback feedback = giftFeedbackMapper.selectOne(new LambdaQueryWrapper<GiftFeedback>().eq(GiftFeedback::getGiftRequestId, vo.getGiftRequestId()).last("limit 1"));
        if (feedback == null) {
            feedback = new GiftFeedback();
            feedback.setGiftRequestId(vo.getGiftRequestId());
            feedback.setFeedbackOpenId(vo.getOpenId());
            feedback.setCreateTime(now);
        }
        feedback.setRating(vo.getRating());
        feedback.setMessage(vo.getMessage());
        feedback.setPreference(vo.getPreference());
        feedback.setParentNote(vo.getParentNote());
        feedback.setModifyTime(now);
        if (feedback.getId() == null) {
            giftFeedbackMapper.insert(feedback);
        } else {
            giftFeedbackMapper.updateById(feedback);
        }
        request.setStatus(GiftStatusEnum.FEEDBACK_DONE.getCode());
        request.setThankYouSentAt(now);
        request.setModifyTime(now);
        giftRequestMapper.updateById(request);
        log(request.getId(), vo.getOpenId(), "feedback", vo.getMessage());
        return detail(request.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GiftRequestDTO cancel(GiftActionVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        GiftRequest request = require(vo.getGiftRequestId());
        ValidatorUtil.checkArgument(isCircleMember(request.getFamilyId(), vo.getOpenId()), "只有圈内成员可以操作愿望");
        if (GiftStatusEnum.CLAIMED.getCode().equals(request.getStatus())) {
            ValidatorUtil.checkArgument(vo.getOpenId().equals(request.getClaimedByOpenId()) || vo.getOpenId().equals(request.getCreatedByOpenId()) || vo.getOpenId().equals(request.getReceiverOpenId()), "无权取消准备");
            request.setStatus(GiftStatusEnum.OPEN.getCode());
            request.setClaimedByOpenId(null);
            request.setClaimedByName(null);
            request.setClaimNote(null);
            request.setClaimedAt(null);
        } else {
            ValidatorUtil.checkArgument(vo.getOpenId().equals(request.getCreatedByOpenId()) || vo.getOpenId().equals(request.getReceiverOpenId()), "只能取消自己的愿望");
            ValidatorUtil.checkArgument(!GiftStatusEnum.CONFIRMED.getCode().equals(request.getStatus()) && !GiftStatusEnum.FEEDBACK_PENDING.getCode().equals(request.getStatus()) && !GiftStatusEnum.FEEDBACK_DONE.getCode().equals(request.getStatus()), "当前状态不可取消");
            request.setStatus(GiftStatusEnum.CANCELED.getCode());
        }
        request.setModifyTime(new Date());
        giftRequestMapper.updateById(request);
        log(request.getId(), vo.getOpenId(), "cancel", vo.getComment());
        return detail(request.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public GiftRequestDTO changeStatus(GiftActionVo vo, String from, String to, String action) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        GiftRequest request = require(vo.getGiftRequestId());
        ValidatorUtil.checkArgument(from.equals(request.getStatus()), "当前状态不允许执行该操作");
        request.setStatus(to);
        Date now = new Date();
        request.setModifyTime(now);
        if (GiftStatusEnum.OPEN.getCode().equals(to) || GiftStatusEnum.REJECTED.getCode().equals(to)) {
            request.setReviewerOpenId(vo.getOpenId());
            request.setReviewedAt(now);
        }
        if (GiftStatusEnum.CONFIRMED.getCode().equals(to)) {
            request.setConfirmedAt(now);
        }
        if (GiftStatusEnum.FEEDBACK_PENDING.getCode().equals(to) || GiftStatusEnum.COMPLETED.getCode().equals(to)) {
            request.setCompletedAt(now);
        }
        if (GiftStatusEnum.FEEDBACK_DONE.getCode().equals(to) || GiftStatusEnum.THANKED.getCode().equals(to)) {
            request.setThankYouSentAt(now);
        }
        giftRequestMapper.updateById(request);
        log(request.getId(), vo.getOpenId(), action, vo.getComment());
        return detail(request.getId());
    }


    private void requireFamilyParent(Integer giftRequestId, String openId) {
        ValidatorUtil.checkNotBlank(openId, "openId 不能为空");
        GiftRequest request = require(giftRequestId);
        Family family = familyMapper.selectById(request.getFamilyId());
        ValidatorUtil.checkNotNull(family, "圈子不存在");
        if ("family".equals(family.getCircleType())) {
            ValidatorUtil.checkArgument(isParent(family.getId(), openId) && isCircleMember(family.getId(), openId), "只有家庭圈家长可以执行该操作");
        } else {
            ValidatorUtil.checkArgument(isCircleMember(family.getId(), openId), "只有圈内成员可以执行该操作");
        }
    }

    private void requireClaimConfirmOperator(Integer giftRequestId, String openId) {
        ValidatorUtil.checkNotBlank(openId, "openId 不能为空");
        GiftRequest request = require(giftRequestId);
        Family family = familyMapper.selectById(request.getFamilyId());
        ValidatorUtil.checkNotNull(family, "圈子不存在");
        if ("family".equals(family.getCircleType())) {
            ValidatorUtil.checkArgument(isParent(family.getId(), openId) && isCircleMember(family.getId(), openId), "只有家庭圈家长可以执行该操作");
        } else {
            ValidatorUtil.checkArgument(isCircleMember(family.getId(), openId), "只有圈内成员可以执行该操作");
            ValidatorUtil.checkArgument(!openId.equals(request.getClaimedByOpenId()), "准备人不能自己确认");
        }
    }

    private boolean canFeedback(GiftRequest request, String openId) {
        if (!isCircleMember(request.getFamilyId(), openId)) {
            return false;
        }
        return openId.equals(request.getReceiverOpenId()) || openId.equals(request.getCreatedByOpenId()) || isParent(request.getFamilyId(), openId);
    }

    private String circleMemberRole(Family family, String openId) {
        if (family == null || org.apache.commons.lang3.StringUtils.isBlank(openId)) {
            return "relative";
        }
        if (openId.equals(family.getOwnerOpenId())) {
            return org.apache.commons.lang3.StringUtils.defaultIfBlank(family.getOwnerRole(), "family".equals(family.getCircleType()) ? "parent" : "relative");
        }
        FamilyMember member = familyMemberMapper.selectOne(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, family.getId())
                .eq(FamilyMember::getMemberOpenId, openId)
                .last("limit 1"));
        return member == null ? "relative" : org.apache.commons.lang3.StringUtils.defaultIfBlank(member.getMemberRole(), "relative");
    }

    private boolean isParent(String openId) {
        return false;
    }

    private boolean isParent(Integer familyId, String openId) {
        Family family = familyMapper.selectById(familyId);
        if (family == null || org.apache.commons.lang3.StringUtils.isBlank(openId)) {
            return false;
        }
        if (openId.equals(family.getOwnerOpenId())) {
            return isParentRole(family.getOwnerRole());
        }
        FamilyMember member = familyMemberMapper.selectOne(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, familyId)
                .eq(FamilyMember::getMemberOpenId, openId)
                .last("limit 1"));
        return member != null && isParentRole(member.getMemberRole());
    }

    private boolean isParentRole(String role) {
        return "parent".equals(role)
                || "dad".equals(role)
                || "mom".equals(role)
                || "grandpa".equals(role)
                || "grandma".equals(role)
                || "maternal_grandpa".equals(role)
                || "maternal_grandma".equals(role);
    }

    private boolean isChildRole(String role) {
        return "child".equals(role) || "boy".equals(role) || "girl".equals(role);
    }

    private boolean isCircleMember(Integer familyId, String openId) {
        if (familyId == null || org.apache.commons.lang3.StringUtils.isBlank(openId)) {
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

    private GiftRequest require(Integer id) {
        ValidatorUtil.checkNotNull(id, "giftRequestId 不能为空");
        GiftRequest request = giftRequestMapper.selectById(id);
        ValidatorUtil.checkNotNull(request, "愿望不存在");
        return request;
    }

    private void log(Integer giftRequestId, String openId, String action, String comment) {
        AuditLog log = new AuditLog();
        log.setGiftRequestId(giftRequestId);
        log.setOperatorOpenId(openId);
        log.setAction(action);
        log.setComment(comment);
        log.setCreateTime(new Date());
        auditLogMapper.insert(log);
    }

    private Map<Integer, String> childNameMap(List<GiftRequest> requests) {
        List<Integer> childIds = requests.stream().map(GiftRequest::getChildId).filter(id -> id != null).distinct().collect(Collectors.toList());
        if (childIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        return childMapper.selectBatchIds(childIds).stream().collect(Collectors.toMap(Child::getId, Child::getChildName));
    }

    private GiftRequestDTO toDto(GiftRequest request, Map<Integer, String> childMap) {
        GiftRequestDTO dto = new GiftRequestDTO();
        BeanUtils.copyProperties(request, dto);
        dto.setChildName(childMap.get(request.getChildId()));
        dto.setReceiverName(request.getReceiverName() == null ? childMap.get(request.getChildId()) : request.getReceiverName());
        dto.setReceiverOpenId(request.getReceiverOpenId());
        dto.setTargetOpenId(request.getTargetOpenId());
        dto.setTargetName(request.getTargetName());
        dto.setStatusText(statusText(request.getStatus()));
        GiftFeedback feedback = giftFeedbackMapper.selectOne(new LambdaQueryWrapper<GiftFeedback>().eq(GiftFeedback::getGiftRequestId, request.getId()).last("limit 1"));
        if (feedback != null) {
            dto.setFeedbackRating(feedback.getRating());
            dto.setFeedbackMessage(feedback.getMessage());
            dto.setFeedbackPreference(feedback.getPreference());
            dto.setFeedbackParentNote(feedback.getParentNote());
        }
        return dto;
    }

    private String statusText(String status) {
        for (GiftStatusEnum value : GiftStatusEnum.values()) {
            if (value.getCode().equals(status)) {
                return value.getText();
            }
        }
        return status;
    }
}
