package com.wx.gift.controller;

import com.wx.gift.dto.SchulteRecordDTO;
import com.wx.gift.service.MiniGameService;
import com.wx.gift.vo.SchulteRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/minigame")
public class MiniGameController {
    @Autowired
    private MiniGameService miniGameService;

    @RequestMapping("/list")
    public List<Map<String, Object>> list() {
        return miniGameService.listMiniGames();
    }

    @RequestMapping("/schulte/saveRecord")
    public SchulteRecordDTO saveSchulteRecord(@RequestBody SchulteRecordVo vo) {
        return miniGameService.saveSchulteRecord(vo);
    }

    @RequestMapping("/schulte/records")
    public List<SchulteRecordDTO> schulteRecords(@RequestBody SchulteRecordVo vo) {
        return miniGameService.listSchulteRecords(vo);
    }

    @RequestMapping("/schulte/stats")
    public Map<String, Object> schulteStats(@RequestBody SchulteRecordVo vo) {
        return miniGameService.schulteStats(vo);
    }

    @RequestMapping("/schulte/deleteRecord")
    public Boolean deleteSchulteRecord(@RequestBody SchulteRecordVo vo) {
        return miniGameService.deleteSchulteRecord(vo);
    }
}
