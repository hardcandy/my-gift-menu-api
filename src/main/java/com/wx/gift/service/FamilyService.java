package com.wx.gift.service;

import com.wx.gift.model.Child;
import com.wx.gift.model.Family;
import com.wx.gift.vo.ChildSaveVo;
import com.wx.gift.vo.FamilySaveVo;
import com.wx.gift.vo.FamilyJoinVo;
import com.wx.gift.vo.CircleInviteVo;
import com.wx.gift.dto.CircleInviteDTO;
import com.wx.gift.dto.CircleJoinRequestDTO;
import com.wx.gift.vo.CircleJoinRequestVo;

import java.util.List;
import java.util.Map;

public interface FamilyService {
    Family ensureFamily(FamilySaveVo vo);
    Family createFamily(FamilySaveVo vo);
    List<Map<String, Object>> listFamilies(String openId);
    Family getFamilyByOpenId(String openId);
    Family getFamilyById(Integer familyId);
    Family joinFamily(FamilyJoinVo vo);
    void leaveFamily(FamilyJoinVo vo);
    void removeMember(FamilyJoinVo vo);
    CircleInviteDTO generateInviteCode(CircleInviteVo vo);
    CircleInviteDTO inviteCodeDetail(CircleInviteVo vo);
    CircleJoinRequestDTO applyJoinByInviteCode(CircleJoinRequestVo vo);
    List<CircleJoinRequestDTO> listPendingJoinRequests(CircleJoinRequestVo vo);
    Family approveJoinRequest(CircleJoinRequestVo vo);
    Family joinByInviteCode(CircleInviteVo vo);
    Child createChild(ChildSaveVo vo);
    List<Child> listChildren(String openId, Integer familyId);
    List<Map<String, Object>> listMembers(String openId, Integer familyId);
}
