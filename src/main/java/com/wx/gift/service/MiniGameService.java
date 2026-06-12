package com.wx.gift.service;

import com.wx.gift.dto.SchulteRecordDTO;
import com.wx.gift.vo.SchulteRecordVo;

import java.util.List;
import java.util.Map;

public interface MiniGameService {
    List<Map<String, Object>> listMiniGames();
    SchulteRecordDTO saveSchulteRecord(SchulteRecordVo vo);
    List<SchulteRecordDTO> listSchulteRecords(SchulteRecordVo vo);
    Map<String, Object> schulteStats(SchulteRecordVo vo);
    Boolean deleteSchulteRecord(SchulteRecordVo vo);
}
