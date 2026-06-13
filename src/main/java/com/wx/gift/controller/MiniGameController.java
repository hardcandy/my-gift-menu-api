package com.wx.gift.controller;

import com.wx.gift.dto.SchulteRecordDTO;
import com.wx.gift.service.MiniGameService;
import com.wx.gift.vo.BlocksVo;
import com.wx.gift.vo.GomokuGameVo;
import com.wx.gift.vo.NimGameVo;
import com.wx.gift.vo.PrisonerGameVo;
import com.wx.gift.vo.RiverCrossingVo;
import com.wx.gift.vo.SchulteRecordVo;
import com.wx.gift.vo.TangramVo;
import com.wx.gift.vo.WordDetectiveVo;
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

    @RequestMapping("/word/pack/list")
    public List<Map<String, Object>> listWordPacks(@RequestBody WordDetectiveVo vo) {
        return miniGameService.listWordPacks(vo);
    }

    @RequestMapping("/word/pack/save")
    public Map<String, Object> saveWordPack(@RequestBody WordDetectiveVo vo) {
        return miniGameService.saveWordPack(vo);
    }

    @RequestMapping("/word/pack/delete")
    public Boolean deleteWordPack(@RequestBody WordDetectiveVo vo) {
        return miniGameService.deleteWordPack(vo);
    }

    @RequestMapping("/word/item/list")
    public List<Map<String, Object>> listWordItems(@RequestBody WordDetectiveVo vo) {
        return miniGameService.listWordItems(vo);
    }

    @RequestMapping("/word/item/save")
    public Map<String, Object> saveWordItem(@RequestBody WordDetectiveVo vo) {
        return miniGameService.saveWordItem(vo);
    }

    @RequestMapping("/word/item/batchImport")
    public List<Map<String, Object>> batchImportWordItems(@RequestBody WordDetectiveVo vo) {
        return miniGameService.batchImportWordItems(vo);
    }

    @RequestMapping("/word/item/delete")
    public Boolean deleteWordItem(@RequestBody WordDetectiveVo vo) {
        return miniGameService.deleteWordItem(vo);
    }

    @RequestMapping("/word/play/save")
    public Map<String, Object> saveWordPlayRecord(@RequestBody WordDetectiveVo vo) {
        return miniGameService.saveWordPlayRecord(vo);
    }

    @RequestMapping("/word/report")
    public Map<String, Object> wordReport(@RequestBody WordDetectiveVo vo) {
        return miniGameService.wordReport(vo);
    }

    @RequestMapping("/word/wrongBook")
    public List<Map<String, Object>> wordWrongBook(@RequestBody WordDetectiveVo vo) {
        return miniGameService.wordWrongBook(vo);
    }

    @RequestMapping("/gomoku/create")
    public Map<String, Object> createGomokuGame(@RequestBody GomokuGameVo vo) {
        return miniGameService.createGomokuGame(vo);
    }

    @RequestMapping("/gomoku/join")
    public Map<String, Object> joinGomokuGame(@RequestBody GomokuGameVo vo) {
        return miniGameService.joinGomokuGame(vo);
    }

    @RequestMapping("/gomoku/detail")
    public Map<String, Object> gomokuGameDetail(@RequestBody GomokuGameVo vo) {
        return miniGameService.gomokuGameDetail(vo);
    }

    @RequestMapping("/gomoku/move")
    public Map<String, Object> gomokuMove(@RequestBody GomokuGameVo vo) {
        return miniGameService.gomokuMove(vo);
    }

    @RequestMapping("/gomoku/restart")
    public Map<String, Object> restartGomokuGame(@RequestBody GomokuGameVo vo) {
        return miniGameService.restartGomokuGame(vo);
    }

    @RequestMapping("/gomoku/leaderboard")
    public List<Map<String, Object>> gomokuLeaderboard(@RequestBody GomokuGameVo vo) {
        return miniGameService.gomokuLeaderboard(vo);
    }

    @RequestMapping("/river/saveRecord")
    public Map<String, Object> saveRiverCrossingRecord(@RequestBody RiverCrossingVo vo) {
        return miniGameService.saveRiverCrossingRecord(vo);
    }

    @RequestMapping("/river/leaderboard")
    public List<Map<String, Object>> riverCrossingLeaderboard(@RequestBody RiverCrossingVo vo) {
        return miniGameService.riverCrossingLeaderboard(vo);
    }

    @RequestMapping("/tangram/saveRecord")
    public Map<String, Object> saveTangramRecord(@RequestBody TangramVo vo) {
        return miniGameService.saveTangramRecord(vo);
    }

    @RequestMapping("/tangram/leaderboard")
    public List<Map<String, Object>> tangramLeaderboard(@RequestBody TangramVo vo) {
        return miniGameService.tangramLeaderboard(vo);
    }

    @RequestMapping("/blocks/saveRecord")
    public Map<String, Object> saveBlocksRecord(@RequestBody BlocksVo vo) {
        return miniGameService.saveBlocksRecord(vo);
    }

    @RequestMapping("/blocks/leaderboard")
    public List<Map<String, Object>> blocksLeaderboard(@RequestBody BlocksVo vo) {
        return miniGameService.blocksLeaderboard(vo);
    }

    @RequestMapping("/nim/create")
    public Map<String, Object> createNimGame(@RequestBody NimGameVo vo) {
        return miniGameService.createNimGame(vo);
    }

    @RequestMapping("/nim/join")
    public Map<String, Object> joinNimGame(@RequestBody NimGameVo vo) {
        return miniGameService.joinNimGame(vo);
    }

    @RequestMapping("/nim/random")
    public Map<String, Object> randomNimGame(@RequestBody NimGameVo vo) {
        return miniGameService.randomNimGame(vo);
    }

    @RequestMapping("/nim/cancelMatch")
    public Boolean cancelNimMatch(@RequestBody NimGameVo vo) {
        return miniGameService.cancelNimMatch(vo);
    }

    @RequestMapping("/nim/detail")
    public Map<String, Object> nimGameDetail(@RequestBody NimGameVo vo) {
        return miniGameService.nimGameDetail(vo);
    }

    @RequestMapping("/nim/start")
    public Map<String, Object> startNimGame(@RequestBody NimGameVo vo) {
        return miniGameService.startNimGame(vo);
    }

    @RequestMapping("/nim/take")
    public Map<String, Object> nimTake(@RequestBody NimGameVo vo) {
        return miniGameService.nimTake(vo);
    }

    @RequestMapping("/nim/restart")
    public Map<String, Object> restartNimGame(@RequestBody NimGameVo vo) {
        return miniGameService.restartNimGame(vo);
    }

    @RequestMapping("/nim/saveSolo")
    public Map<String, Object> saveNimSoloGame(@RequestBody NimGameVo vo) {
        return miniGameService.saveNimSoloGame(vo);
    }

    @RequestMapping("/nim/leaderboard")
    public List<Map<String, Object>> nimLeaderboard(@RequestBody NimGameVo vo) {
        return miniGameService.nimLeaderboard(vo);
    }

    @RequestMapping("/prisoner/create")
    public Map<String, Object> createPrisonerGame(@RequestBody PrisonerGameVo vo) {
        return miniGameService.createPrisonerGame(vo);
    }

    @RequestMapping("/prisoner/join")
    public Map<String, Object> joinPrisonerGame(@RequestBody PrisonerGameVo vo) {
        return miniGameService.joinPrisonerGame(vo);
    }

    @RequestMapping("/prisoner/random")
    public Map<String, Object> randomPrisonerGame(@RequestBody PrisonerGameVo vo) {
        return miniGameService.randomPrisonerGame(vo);
    }

    @RequestMapping("/prisoner/cancelMatch")
    public Boolean cancelPrisonerMatch(@RequestBody PrisonerGameVo vo) {
        return miniGameService.cancelPrisonerMatch(vo);
    }

    @RequestMapping("/prisoner/detail")
    public Map<String, Object> prisonerGameDetail(@RequestBody PrisonerGameVo vo) {
        return miniGameService.prisonerGameDetail(vo);
    }

    @RequestMapping("/prisoner/start")
    public Map<String, Object> startPrisonerGame(@RequestBody PrisonerGameVo vo) {
        return miniGameService.startPrisonerGame(vo);
    }

    @RequestMapping("/prisoner/choose")
    public Map<String, Object> prisonerChoose(@RequestBody PrisonerGameVo vo) {
        return miniGameService.prisonerChoose(vo);
    }

    @RequestMapping("/prisoner/restart")
    public Map<String, Object> restartPrisonerGame(@RequestBody PrisonerGameVo vo) {
        return miniGameService.restartPrisonerGame(vo);
    }

    @RequestMapping("/prisoner/saveSolo")
    public Map<String, Object> savePrisonerSoloGame(@RequestBody PrisonerGameVo vo) {
        return miniGameService.savePrisonerSoloGame(vo);
    }

    @RequestMapping("/prisoner/leaderboard")
    public List<Map<String, Object>> prisonerLeaderboard(@RequestBody PrisonerGameVo vo) {
        return miniGameService.prisonerLeaderboard(vo);
    }
}
