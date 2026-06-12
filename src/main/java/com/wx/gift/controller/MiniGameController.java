package com.wx.gift.controller;

import com.wx.gift.dto.SchulteRecordDTO;
import com.wx.gift.service.MiniGameService;
import com.wx.gift.vo.SchulteRecordVo;
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
}
