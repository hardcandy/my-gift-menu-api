package com.wx.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.gift.mapper.ChildMapper;
import com.wx.gift.mapper.CircleInviteMapper;
import com.wx.gift.mapper.CircleJoinRequestMapper;
import com.wx.gift.mapper.BaseUserMapper;
import com.wx.gift.mapper.FamilyMapper;
import com.wx.gift.mapper.FamilyMemberMapper;
import com.wx.gift.model.Child;
import com.wx.gift.model.CircleInvite;
import com.wx.gift.model.CircleJoinRequest;
import com.wx.gift.model.BaseUser;
import com.wx.gift.model.Family;
import com.wx.gift.model.FamilyMember;
import com.wx.gift.service.FamilyService;
import com.wx.gift.util.ValidatorUtil;
import com.wx.gift.dto.CircleInviteDTO;
import com.wx.gift.dto.CircleJoinRequestDTO;
import com.wx.gift.vo.ChildSaveVo;
import com.wx.gift.vo.FamilySaveVo;
import com.wx.gift.vo.FamilyJoinVo;
import com.wx.gift.vo.CircleInviteVo;
import com.wx.gift.vo.CircleJoinRequestVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FamilyServiceImpl implements FamilyService {
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private ChildMapper childMapper;
    @Autowired
    private FamilyMemberMapper familyMemberMapper;
    @Autowired
    private CircleInviteMapper circleInviteMapper;
    @Autowired
    private CircleJoinRequestMapper circleJoinRequestMapper;
    @Autowired
    private BaseUserMapper baseUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Family ensureFamily(FamilySaveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        Family family = getFamilyByOpenId(vo.getOpenId());
        if (family != null) {
            return family;
        }
        return createFamily(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Family createFamily(FamilySaveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        Date now = new Date();
        Family family = new Family();
        String circleType = StringUtils.defaultIfBlank(vo.getCircleType(), "family");
        String ownerRole = "family".equals(circleType) ? vo.getOwnerRole() : "relative";
        ValidatorUtil.checkArgument(!"family".equals(circleType) || StringUtils.isNotBlank(ownerRole), "家庭圈需要选择你的圈内角色");
        family.setOwnerOpenId(vo.getOpenId());
        family.setOwnerRole(StringUtils.defaultIfBlank(normalizeMemberRole(ownerRole), "relative"));
        family.setFamilyName(StringUtils.defaultIfBlank(vo.getFamilyName(), "我的圈子"));
        family.setCircleType(circleType);
        family.setStatus("active");
        family.setCreateTime(now);
        family.setModifyTime(now);
        familyMapper.insert(family);
        return familyMapper.selectById(family.getId());
    }

    @Override
    public List<Map<String, Object>> listFamilies(String openId) {
        ValidatorUtil.checkNotBlank(openId, "openId 不能为空");
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        List<Family> owned = familyMapper.selectList(new LambdaQueryWrapper<Family>()
                .eq(Family::getOwnerOpenId, openId)
                .eq(Family::getStatus, "active")
                .orderByDesc(Family::getId));
        for (Family family : owned) {
            result.add(toFamilyMap(family, "owner", ownerRole(family)));
        }
        List<FamilyMember> memberships = familyMemberMapper.selectList(new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getMemberOpenId, openId).orderByDesc(FamilyMember::getId));
        for (FamilyMember member : memberships) {
            Family family = familyMapper.selectById(member.getFamilyId());
            if (isActiveFamily(family) && !openId.equals(family.getOwnerOpenId())) {
                result.add(toFamilyMap(family, "member", StringUtils.defaultIfBlank(member.getMemberRole(), "relative")));
            }
        }
        return result;
    }

    @Override
    public Family getFamilyByOpenId(String openId) {
        Family owned = familyMapper.selectOne(new LambdaQueryWrapper<Family>()
                .eq(Family::getOwnerOpenId, openId)
                .eq(Family::getStatus, "active")
                .last("limit 1"));
        if (owned != null) {
            return owned;
        }
        FamilyMember member = familyMemberMapper.selectOne(new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getMemberOpenId, openId).last("limit 1"));
        Family family = member == null ? null : familyMapper.selectById(member.getFamilyId());
        return isActiveFamily(family) ? family : null;
    }

    @Override
    public Family getFamilyById(Integer familyId) {
        ValidatorUtil.checkNotNull(familyId, "familyId 不能为空");
        Family family = familyMapper.selectById(familyId);
        return isActiveFamily(family) ? family : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Family joinFamily(FamilyJoinVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        Family family = familyMapper.selectById(vo.getFamilyId());
        ValidatorUtil.checkArgument(isActiveFamily(family), "家庭不存在");
        if (vo.getOpenId().equals(family.getOwnerOpenId())) {
            return family;
        }
        FamilyMember existing = familyMemberMapper.selectOne(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, vo.getFamilyId())
                .eq(FamilyMember::getMemberOpenId, vo.getOpenId())
                .last("limit 1"));
        if (existing == null) {
            Date now = new Date();
            FamilyMember member = new FamilyMember();
            member.setFamilyId(vo.getFamilyId());
            member.setMemberOpenId(vo.getOpenId());
            member.setMemberRole(memberRoleForJoin(family, vo.getRole()));
            member.setCreateTime(now);
            member.setModifyTime(now);
            familyMemberMapper.insert(member);
        }
        return family;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaveFamily(FamilyJoinVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        Family family = familyMapper.selectById(vo.getFamilyId());
        ValidatorUtil.checkArgument(isActiveFamily(family), "圈子不存在");
        ValidatorUtil.checkArgument(!vo.getOpenId().equals(family.getOwnerOpenId()), "圈主不能直接退出自己创建的圈子");
        familyMemberMapper.delete(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, vo.getFamilyId())
                .eq(FamilyMember::getMemberOpenId, vo.getOpenId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFamily(FamilyJoinVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        Family family = familyMapper.selectById(vo.getFamilyId());
        ValidatorUtil.checkArgument(isActiveFamily(family), "圈子不存在");
        ValidatorUtil.checkArgument(vo.getOpenId().equals(family.getOwnerOpenId()), "只有圈主可以删除圈子");
        family.setStatus("deleted");
        family.setModifyTime(new Date());
        familyMapper.updateById(family);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(FamilyJoinVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getMemberOpenId(), "memberOpenId 不能为空");
        Family family = familyMapper.selectById(vo.getFamilyId());
        ValidatorUtil.checkArgument(isActiveFamily(family), "圈子不存在");
        ValidatorUtil.checkArgument(vo.getOpenId().equals(family.getOwnerOpenId()), "只有圈主可以删除成员");
        ValidatorUtil.checkArgument(!vo.getMemberOpenId().equals(family.getOwnerOpenId()), "不能删除圈主");
        familyMemberMapper.delete(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, vo.getFamilyId())
                .eq(FamilyMember::getMemberOpenId, vo.getMemberOpenId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberRole(FamilyJoinVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getMemberOpenId(), "memberOpenId 不能为空");
        Family family = familyMapper.selectById(vo.getFamilyId());
        ValidatorUtil.checkArgument(isActiveFamily(family), "圈子不存在");
        ValidatorUtil.checkArgument(vo.getOpenId().equals(family.getOwnerOpenId()), "只有圈主可以修改成员角色");
        String role = memberRoleForJoin(family, vo.getRole());
        Date now = new Date();
        if (vo.getMemberOpenId().equals(family.getOwnerOpenId())) {
            family.setOwnerRole(role);
            family.setModifyTime(now);
            familyMapper.updateById(family);
            return;
        }
        FamilyMember member = familyMemberMapper.selectOne(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, vo.getFamilyId())
                .eq(FamilyMember::getMemberOpenId, vo.getMemberOpenId())
                .last("limit 1"));
        ValidatorUtil.checkNotNull(member, "成员不存在");
        member.setMemberRole(role);
        member.setModifyTime(now);
        familyMemberMapper.updateById(member);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CircleInviteDTO generateInviteCode(CircleInviteVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        Family family = familyMapper.selectById(vo.getFamilyId());
        ValidatorUtil.checkArgument(isActiveFamily(family), "圈子不存在");
        ValidatorUtil.checkArgument(isCircleMember(vo.getFamilyId(), vo.getOpenId()), "只有圈内成员可以生成邀请码");
        Date now = new Date();
        circleInviteMapper.delete(new LambdaQueryWrapper<CircleInvite>().le(CircleInvite::getExpireTime, now));
        CircleInvite invite = new CircleInvite();
        invite.setFamilyId(vo.getFamilyId());
        invite.setInviteCode(nextInviteCode());
        invite.setCreatedByOpenId(vo.getOpenId());
        invite.setExpireTime(new Date(now.getTime() + 10L * 60 * 1000));
        invite.setCreateTime(now);
        invite.setModifyTime(now);
        circleInviteMapper.insert(invite);
        return toInviteDto(invite, family);
    }

    @Override
    public CircleInviteDTO inviteCodeDetail(CircleInviteVo vo) {
        ValidatorUtil.checkNotBlank(vo.getInviteCode(), "邀请码不能为空");
        CircleInvite invite = getValidInvite(vo.getInviteCode());
        Family family = familyMapper.selectById(invite.getFamilyId());
        ValidatorUtil.checkArgument(isActiveFamily(family), "圈子不存在");
        return toInviteDto(invite, family);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Family joinByInviteCode(CircleInviteVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getInviteCode(), "邀请码不能为空");
        CircleInvite invite = getValidInvite(vo.getInviteCode());
        Family family = familyMapper.selectById(invite.getFamilyId());
        ValidatorUtil.checkArgument(isActiveFamily(family), "圈子不存在");
        FamilyJoinVo joinVo = new FamilyJoinVo();
        joinVo.setOpenId(vo.getOpenId());
        joinVo.setFamilyId(invite.getFamilyId());
        joinVo.setRole(memberRoleForJoin(family, "relative"));
        return joinFamily(joinVo);
    }

    private CircleInvite getValidInvite(String inviteCode) {
        CircleInvite invite = circleInviteMapper.selectOne(new LambdaQueryWrapper<CircleInvite>()
                .eq(CircleInvite::getInviteCode, inviteCode)
                .orderByDesc(CircleInvite::getId)
                .last("limit 1"));
        ValidatorUtil.checkNotNull(invite, "邀请码不存在");
        ValidatorUtil.checkArgument(invite.getExpireTime().after(new Date()), "邀请码已过期");
        return invite;
    }

    private String nextInviteCode() {
        for (int i = 0; i < 50; i++) {
            String code = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
            CircleInvite existing = circleInviteMapper.selectOne(new LambdaQueryWrapper<CircleInvite>()
                    .eq(CircleInvite::getInviteCode, code)
                    .last("limit 1"));
            if (existing == null) {
                return code;
            }
        }
        throw new IllegalArgumentException("邀请码生成失败，请稍后再试");
    }

    private CircleInviteDTO toInviteDto(CircleInvite invite, Family family) {
        CircleInviteDTO dto = new CircleInviteDTO();
        dto.setFamilyId(family.getId());
        dto.setFamilyName(family.getFamilyName());
        dto.setCircleType(family.getCircleType());
        dto.setInviteCode(invite.getInviteCode());
        dto.setExpireTime(invite.getExpireTime());
        return dto;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CircleJoinRequestDTO applyJoinByInviteCode(CircleJoinRequestVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getInviteCode(), "邀请码不能为空");
        CircleInvite invite = getValidInvite(vo.getInviteCode());
        Family family = familyMapper.selectById(invite.getFamilyId());
        ValidatorUtil.checkArgument(isActiveFamily(family), "圈子不存在");
        if (vo.getOpenId().equals(family.getOwnerOpenId()) || isCircleMember(invite.getFamilyId(), vo.getOpenId())) {
            throw new IllegalArgumentException("已经在这个圈子里了，不需要重复加入");
        }
        CircleJoinRequest existing = circleJoinRequestMapper.selectOne(new LambdaQueryWrapper<CircleJoinRequest>()
                .eq(CircleJoinRequest::getFamilyId, invite.getFamilyId())
                .eq(CircleJoinRequest::getApplicantOpenId, vo.getOpenId())
                .eq(CircleJoinRequest::getStatus, "pending")
                .last("limit 1"));
        if (existing != null) {
            return toJoinRequestDto(existing, family);
        }
        BaseUser applicant = baseUserMapper.selectOne(new LambdaQueryWrapper<BaseUser>().eq(BaseUser::getOpenId, vo.getOpenId()).last("limit 1"));
        Date now = new Date();
        CircleJoinRequest request = new CircleJoinRequest();
        request.setFamilyId(invite.getFamilyId());
        request.setInviteCode(invite.getInviteCode());
        request.setApplicantOpenId(vo.getOpenId());
        request.setApplicantNickName(applicant == null ? "新成员" : applicant.getNickName());
        request.setRequestedRole("relative");
        request.setStatus("pending");
        request.setCreateTime(now);
        request.setModifyTime(now);
        circleJoinRequestMapper.insert(request);
        return toJoinRequestDto(request, family);
    }

    @Override
    public List<CircleJoinRequestDTO> listPendingJoinRequests(CircleJoinRequestVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkArgument(canApprove(vo.getFamilyId(), vo.getOpenId()), "只有圈内成员可以查看申请");
        Family family = familyMapper.selectById(vo.getFamilyId());
        return circleJoinRequestMapper.selectList(new LambdaQueryWrapper<CircleJoinRequest>()
                        .eq(CircleJoinRequest::getFamilyId, vo.getFamilyId())
                        .eq(CircleJoinRequest::getStatus, "pending")
                        .orderByDesc(CircleJoinRequest::getId))
                .stream().map(item -> toJoinRequestDto(item, family)).collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Family approveJoinRequest(CircleJoinRequestVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getRequestId(), "requestId 不能为空");
        CircleJoinRequest request = circleJoinRequestMapper.selectById(vo.getRequestId());
        ValidatorUtil.checkNotNull(request, "申请不存在");
        ValidatorUtil.checkArgument("pending".equals(request.getStatus()), "申请已处理");
        ValidatorUtil.checkArgument(canApprove(request.getFamilyId(), vo.getOpenId()), "只有圈内成员可以同意申请");
        FamilyJoinVo joinVo = new FamilyJoinVo();
        joinVo.setOpenId(request.getApplicantOpenId());
        joinVo.setFamilyId(request.getFamilyId());
        joinVo.setRole(request.getRequestedRole());
        Family family = joinFamily(joinVo);
        Date now = new Date();
        request.setStatus("approved");
        request.setApproveOpenId(vo.getOpenId());
        request.setApproveTime(now);
        request.setModifyTime(now);
        circleJoinRequestMapper.updateById(request);
        return family;
    }

    private Map<String, Object> toFamilyMap(Family family, String relation, String role) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", family.getId());
        map.put("familyName", family.getFamilyName());
        map.put("circleType", family.getCircleType());
        map.put("ownerOpenId", family.getOwnerOpenId());
        map.put("relation", relation);
        map.put("memberRole", role);
        map.put("createTime", family.getCreateTime());
        map.put("modifyTime", family.getModifyTime());
        return map;
    }

    private String ownerRole(Family family) {
        return StringUtils.defaultIfBlank(family.getOwnerRole(), "family".equals(family.getCircleType()) ? "parent" : "relative");
    }

    private String memberRoleForJoin(Family family, String requestedRole) {
        if (family == null || !"family".equals(family.getCircleType())) {
            return "relative";
        }
        String role = normalizeMemberRole(requestedRole);
        if (StringUtils.isNotBlank(role)) {
            return role;
        }
        return "relative";
    }

    private String normalizeMemberRole(String role) {
        String value = StringUtils.defaultIfBlank(role, "");
        if ("parent".equals(value) || "child".equals(value) || "relative".equals(value)
                || "boy".equals(value) || "girl".equals(value)
                || "dad".equals(value) || "mom".equals(value)
                || "grandpa".equals(value) || "grandma".equals(value)
                || "maternal_grandpa".equals(value) || "maternal_grandma".equals(value)) {
            return value;
        }
        return "";
    }

    private boolean canApprove(Integer familyId, String openId) {
        Family family = familyMapper.selectById(familyId);
        if (!isActiveFamily(family)) {
            return false;
        }
        return openId.equals(family.getOwnerOpenId()) || isCircleMember(familyId, openId);
    }

    private boolean isCircleMember(Integer familyId, String openId) {
        Family family = familyMapper.selectById(familyId);
        if (!isActiveFamily(family)) {
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

    private CircleJoinRequestDTO toJoinRequestDto(CircleJoinRequest request, Family family) {
        CircleJoinRequestDTO dto = new CircleJoinRequestDTO();
        dto.setId(request.getId());
        dto.setFamilyId(request.getFamilyId());
        dto.setFamilyName(family == null ? null : family.getFamilyName());
        dto.setCircleType(family == null ? null : family.getCircleType());
        dto.setApplicantOpenId(request.getApplicantOpenId());
        dto.setApplicantNickName(request.getApplicantNickName());
        dto.setRequestedRole(request.getRequestedRole());
        dto.setStatus(request.getStatus());
        dto.setCreateTime(request.getCreateTime());
        return dto;
    }

    @Override
    public List<Map<String, Object>> listMembers(String openId, Integer familyId) {
        ValidatorUtil.checkNotBlank(openId, "openId 不能为空");
        Integer targetFamilyId = familyId;
        if (targetFamilyId == null) {
            Family current = getFamilyByOpenId(openId);
            if (current == null) {
                return java.util.Collections.emptyList();
            }
            targetFamilyId = current.getId();
        }
        Family family = familyMapper.selectById(targetFamilyId);
        ValidatorUtil.checkArgument(isActiveFamily(family), "圈子不存在");
        ValidatorUtil.checkArgument(canApprove(targetFamilyId, openId), "只有圈内成员可以查看成员");
        List<Map<String, Object>> members = new java.util.ArrayList<>();
        members.add(toMemberMap(family.getOwnerOpenId(), ownerRole(family), true));
        List<FamilyMember> rows = familyMemberMapper.selectList(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, targetFamilyId)
                .orderByAsc(FamilyMember::getId));
        for (FamilyMember row : rows) {
            if (!family.getOwnerOpenId().equals(row.getMemberOpenId())) {
                members.add(toMemberMap(row.getMemberOpenId(), StringUtils.defaultIfBlank(row.getMemberRole(), "relative"), false));
            }
        }
        return members;
    }

    private Map<String, Object> toMemberMap(String openId, String role, boolean owner) {
        Map<String, Object> map = new java.util.HashMap<>();
        BaseUser user = baseUserMapper.selectOne(new LambdaQueryWrapper<BaseUser>()
                .eq(BaseUser::getOpenId, openId)
                .last("limit 1"));
        map.put("openId", openId);
        map.put("nickName", user == null ? "微信用户" : StringUtils.defaultIfBlank(user.getNickName(), "微信用户"));
        map.put("avatarUrl", user == null ? "" : StringUtils.defaultString(user.getAvatarUrl()));
        map.put("memberRole", role);
        map.put("relation", owner ? "owner" : "member");
        map.put("owner", owner);
        return map;
    }

    @Override
    public Child createChild(ChildSaveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getChildName(), "孩子昵称不能为空");
        Family family = null;
        if (vo.getFamilyId() != null) {
            family = familyMapper.selectById(vo.getFamilyId());
        }
        if (family == null) {
            FamilySaveVo familySaveVo = new FamilySaveVo();
            familySaveVo.setOpenId(vo.getOpenId());
            family = ensureFamily(familySaveVo);
        }
        ValidatorUtil.checkArgument(isActiveFamily(family), "圈子不存在");
        Date now = new Date();
        Child child = new Child();
        child.setFamilyId(family.getId());
        child.setChildName(vo.getChildName());
        child.setBirthday(vo.getBirthday());
        child.setChildOpenId(vo.getChildOpenId());
        child.setGuardianOpenId(vo.getOpenId());
        child.setCreateTime(now);
        child.setModifyTime(now);
        childMapper.insert(child);
        return childMapper.selectById(child.getId());
    }

    @Override
    public List<Child> listChildren(String openId, Integer familyId) {
        ValidatorUtil.checkNotBlank(openId, "openId 不能为空");
        Integer targetFamilyId = familyId;
        Family targetFamily;
        if (targetFamilyId == null) {
            targetFamily = getFamilyByOpenId(openId);
            if (targetFamily == null) {
                return java.util.Collections.emptyList();
            }
            targetFamilyId = targetFamily.getId();
        } else {
            targetFamily = familyMapper.selectById(targetFamilyId);
        }
        ValidatorUtil.checkArgument(isActiveFamily(targetFamily), "圈子不存在");
        ensureChildrenFromChildMembers(targetFamily, openId);
        return childMapper.selectList(new LambdaQueryWrapper<Child>().eq(Child::getFamilyId, targetFamilyId).orderByDesc(Child::getId));
    }

    private void ensureChildrenFromChildMembers(Family family, String guardianOpenId) {
        if (!"family".equals(family.getCircleType())) {
            return;
        }
        java.util.Set<String> existingOpenIds = childMapper.selectList(new LambdaQueryWrapper<Child>()
                        .eq(Child::getFamilyId, family.getId()))
                .stream()
                .map(Child::getChildOpenId)
                .filter(StringUtils::isNotBlank)
                .collect(java.util.stream.Collectors.toSet());
        Date now = new Date();
        if (isChildRole(ownerRole(family)) && !existingOpenIds.contains(family.getOwnerOpenId())) {
            createChildFromMember(family.getId(), family.getOwnerOpenId(), guardianOpenId, now);
            existingOpenIds.add(family.getOwnerOpenId());
        }
        List<FamilyMember> rows = familyMemberMapper.selectList(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, family.getId())
                .orderByAsc(FamilyMember::getId));
        for (FamilyMember row : rows) {
            String memberOpenId = row.getMemberOpenId();
            if (StringUtils.isBlank(memberOpenId) || existingOpenIds.contains(memberOpenId)) {
                continue;
            }
            if (isChildRole(StringUtils.defaultIfBlank(row.getMemberRole(), "relative"))) {
                createChildFromMember(family.getId(), memberOpenId, guardianOpenId, now);
                existingOpenIds.add(memberOpenId);
            }
        }
    }

    private boolean isChildRole(String role) {
        return "child".equals(role) || "boy".equals(role) || "girl".equals(role);
    }

    private void createChildFromMember(Integer familyId, String childOpenId, String guardianOpenId, Date now) {
        BaseUser user = baseUserMapper.selectOne(new LambdaQueryWrapper<BaseUser>()
                .eq(BaseUser::getOpenId, childOpenId)
                .last("limit 1"));
        Child child = new Child();
        child.setFamilyId(familyId);
        child.setChildOpenId(childOpenId);
        child.setChildName(user == null ? "孩子" : StringUtils.defaultIfBlank(user.getNickName(), "孩子"));
        child.setBirthday("");
        child.setGuardianOpenId(guardianOpenId);
        child.setCreateTime(now);
        child.setModifyTime(now);
        childMapper.insert(child);
    }

    private boolean isActiveFamily(Family family) {
        return family != null && !"deleted".equals(family.getStatus());
    }
}
