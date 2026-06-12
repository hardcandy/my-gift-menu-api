package com.wx.gift.service;

import com.wx.gift.dto.SchulteRecordDTO;
import com.wx.gift.vo.SchulteRecordVo;
import com.wx.gift.vo.WordDetectiveVo;

import java.util.List;
import java.util.Map;

public interface MiniGameService {
    List<Map<String, Object>> listMiniGames();
    SchulteRecordDTO saveSchulteRecord(SchulteRecordVo vo);
    List<SchulteRecordDTO> listSchulteRecords(SchulteRecordVo vo);
    Map<String, Object> schulteStats(SchulteRecordVo vo);
    Boolean deleteSchulteRecord(SchulteRecordVo vo);
    List<Map<String, Object>> listWordPacks(WordDetectiveVo vo);
    Map<String, Object> saveWordPack(WordDetectiveVo vo);
    Boolean deleteWordPack(WordDetectiveVo vo);
    List<Map<String, Object>> listWordItems(WordDetectiveVo vo);
    Map<String, Object> saveWordItem(WordDetectiveVo vo);
    List<Map<String, Object>> batchImportWordItems(WordDetectiveVo vo);
    Boolean deleteWordItem(WordDetectiveVo vo);
    Map<String, Object> saveWordPlayRecord(WordDetectiveVo vo);
    Map<String, Object> wordReport(WordDetectiveVo vo);
    List<Map<String, Object>> wordWrongBook(WordDetectiveVo vo);
}
