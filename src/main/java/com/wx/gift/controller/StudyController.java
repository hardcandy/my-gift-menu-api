package com.wx.gift.controller;

import com.wx.gift.dto.StudyItemDTO;
import com.wx.gift.dto.StudyRecordDTO;
import com.wx.gift.dto.StudySubjectDTO;
import com.wx.gift.service.StudyService;
import com.wx.gift.vo.StudyItemVo;
import com.wx.gift.vo.StudyRecordVo;
import com.wx.gift.vo.StudySubjectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/study")
public class StudyController {
    @Autowired
    private StudyService studyService;

    @RequestMapping("/subject/list")
    public List<StudySubjectDTO> listSubjects(@RequestBody StudySubjectVo vo) { return studyService.listSubjects(vo); }

    @RequestMapping("/subject/save")
    public StudySubjectDTO saveSubject(@RequestBody StudySubjectVo vo) { return studyService.saveSubject(vo); }

    @RequestMapping("/subject/delete")
    public Boolean deleteSubject(@RequestBody StudySubjectVo vo) { return studyService.deleteSubject(vo); }

    @RequestMapping("/item/list")
    public List<StudyItemDTO> listItems(@RequestBody StudyItemVo vo) { return studyService.listItems(vo); }

    @RequestMapping("/item/detail")
    public StudyItemDTO itemDetail(@RequestBody StudyItemVo vo) { return studyService.itemDetail(vo); }

    @RequestMapping("/item/save")
    public StudyItemDTO saveItem(@RequestBody StudyItemVo vo) { return studyService.saveItem(vo); }

    @RequestMapping("/item/delete")
    public Boolean deleteItem(@RequestBody StudyItemVo vo) { return studyService.deleteItem(vo); }

    @RequestMapping("/record/list")
    public List<StudyRecordDTO> listRecords(@RequestBody StudyRecordVo vo) { return studyService.listRecords(vo); }

    @RequestMapping("/record/detail")
    public StudyRecordDTO recordDetail(@RequestBody StudyRecordVo vo) { return studyService.recordDetail(vo); }

    @RequestMapping("/record/save")
    public StudyRecordDTO saveRecord(@RequestBody StudyRecordVo vo) { return studyService.saveRecord(vo); }

    @RequestMapping("/record/delete")
    public Boolean deleteRecord(@RequestBody StudyRecordVo vo) { return studyService.deleteRecord(vo); }

    @RequestMapping("/record/markCorrected")
    public StudyRecordDTO markCorrected(@RequestBody StudyRecordVo vo) { return studyService.markCorrected(vo); }

    @RequestMapping("/correction/list")
    public List<StudyRecordDTO> listCorrections(@RequestBody StudyRecordVo vo) { return studyService.listCorrections(vo); }

    @RequestMapping("/stat/summary")
    public Map<String, Object> summary(@RequestBody StudyRecordVo vo) { return studyService.summary(vo); }

    @RequestMapping("/stat/trend")
    public Map<String, Object> trend(@RequestBody StudyRecordVo vo) { return studyService.trend(vo); }

    @RequestMapping("/stat/compare")
    public Map<String, Object> compare(@RequestBody StudyRecordVo vo) { return studyService.compare(vo); }
}
