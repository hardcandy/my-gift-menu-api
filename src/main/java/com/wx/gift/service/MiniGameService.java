package com.wx.gift.service;

import com.wx.gift.dto.SchulteRecordDTO;
import com.wx.gift.vo.GomokuGameVo;
import com.wx.gift.vo.NimGameVo;
import com.wx.gift.vo.RiverCrossingVo;
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
    Map<String, Object> createGomokuGame(GomokuGameVo vo);
    Map<String, Object> joinGomokuGame(GomokuGameVo vo);
    Map<String, Object> gomokuGameDetail(GomokuGameVo vo);
    Map<String, Object> gomokuMove(GomokuGameVo vo);
    Map<String, Object> restartGomokuGame(GomokuGameVo vo);
    List<Map<String, Object>> gomokuLeaderboard(GomokuGameVo vo);
    Map<String, Object> saveRiverCrossingRecord(RiverCrossingVo vo);
    List<Map<String, Object>> riverCrossingLeaderboard(RiverCrossingVo vo);
    Map<String, Object> createNimGame(NimGameVo vo);
    Map<String, Object> joinNimGame(NimGameVo vo);
    Map<String, Object> randomNimGame(NimGameVo vo);
    Boolean cancelNimMatch(NimGameVo vo);
    Map<String, Object> nimGameDetail(NimGameVo vo);
    Map<String, Object> startNimGame(NimGameVo vo);
    Map<String, Object> nimTake(NimGameVo vo);
    Map<String, Object> restartNimGame(NimGameVo vo);
    Map<String, Object> saveNimSoloGame(NimGameVo vo);
    List<Map<String, Object>> nimLeaderboard(NimGameVo vo);
}
