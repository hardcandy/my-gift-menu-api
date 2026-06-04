package com.wx.gift.controller;

import com.wx.gift.dto.CircleInviteDTO;
import com.wx.gift.dto.CircleJoinRequestDTO;
import com.wx.gift.model.Child;
import com.wx.gift.model.Family;
import com.wx.gift.service.FamilyService;
import com.wx.gift.vo.ChildSaveVo;
import com.wx.gift.vo.CircleInviteVo;
import com.wx.gift.vo.CircleJoinRequestVo;
import com.wx.gift.vo.FamilySaveVo;
import com.wx.gift.vo.FamilyJoinVo;
import com.wx.gift.vo.OpenIdVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/family")
public class FamilyController {
    @Autowired
    private FamilyService familyService;

    @RequestMapping("/ensure")
    public Family ensure(@RequestBody FamilySaveVo vo) {
        return familyService.ensureFamily(vo);
    }

    @RequestMapping("/create")
    public Family create(@RequestBody FamilySaveVo vo) {
        return familyService.createFamily(vo);
    }

    @RequestMapping("/listMine")
    public List<Map<String, Object>> listMine(@RequestBody OpenIdVo vo) {
        return familyService.listFamilies(vo.getOpenId());
    }

    @RequestMapping("/detail")
    public Map<String, Object> detail(@RequestBody OpenIdVo vo) {
        Family family = vo.getFamilyId() == null ? familyService.getFamilyByOpenId(vo.getOpenId()) : familyService.getFamilyById(vo.getFamilyId());
        Map<String, Object> result = new HashMap<>();
        result.put("family", family);
        result.put("children", family == null ? java.util.Collections.emptyList() : familyService.listChildren(vo.getOpenId(), family.getId()));
        return result;
    }

    @RequestMapping("/invite/detail")
    public Family inviteDetail(@RequestBody FamilyJoinVo vo) {
        return familyService.getFamilyById(vo.getFamilyId());
    }

    @RequestMapping("/join")
    public Family join(@RequestBody FamilyJoinVo vo) {
        return familyService.joinFamily(vo);
    }

    @RequestMapping("/leave")
    public Boolean leave(@RequestBody FamilyJoinVo vo) {
        familyService.leaveFamily(vo);
        return true;
    }

    @RequestMapping("/member/remove")
    public Boolean removeMember(@RequestBody FamilyJoinVo vo) {
        familyService.removeMember(vo);
        return true;
    }

    @RequestMapping("/invite/generate")
    public CircleInviteDTO generateInvite(@RequestBody CircleInviteVo vo) {
        return familyService.generateInviteCode(vo);
    }

    @RequestMapping("/invite/code/detail")
    public CircleInviteDTO inviteCodeDetail(@RequestBody CircleInviteVo vo) {
        return familyService.inviteCodeDetail(vo);
    }

    @RequestMapping("/invite/code/join")
    public CircleJoinRequestDTO joinByInviteCode(@RequestBody CircleJoinRequestVo vo) {
        return familyService.applyJoinByInviteCode(vo);
    }

    @RequestMapping("/join-request/list")
    public List<CircleJoinRequestDTO> listJoinRequests(@RequestBody CircleJoinRequestVo vo) {
        return familyService.listPendingJoinRequests(vo);
    }

    @RequestMapping("/join-request/approve")
    public Family approveJoinRequest(@RequestBody CircleJoinRequestVo vo) {
        return familyService.approveJoinRequest(vo);
    }

    @RequestMapping("/member/list")
    public List<Map<String, Object>> listMembers(@RequestBody OpenIdVo vo) {
        return familyService.listMembers(vo.getOpenId(), vo.getFamilyId());
    }

    @RequestMapping("/child/create")
    public Child createChild(@RequestBody ChildSaveVo vo) {
        return familyService.createChild(vo);
    }

    @RequestMapping("/child/list")
    public List<Child> listChildren(@RequestBody ChildSaveVo vo) {
        return familyService.listChildren(vo.getOpenId(), vo.getFamilyId());
    }
}
