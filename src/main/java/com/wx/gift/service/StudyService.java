package com.wx.gift.service;

import com.wx.gift.dto.StudyItemDTO;
import com.wx.gift.dto.StudyRecordDTO;
import com.wx.gift.dto.StudySubjectDTO;
import com.wx.gift.vo.StudyItemVo;
import com.wx.gift.vo.StudyRecordVo;
import com.wx.gift.vo.StudySubjectVo;

import java.util.List;
import java.util.Map;

public interface StudyService {
    List<StudySubjectDTO> listSubjects(StudySubjectVo vo);
    StudySubjectDTO saveSubject(StudySubjectVo vo);
    Boolean deleteSubject(StudySubjectVo vo);
    List<StudyItemDTO> listItems(StudyItemVo vo);
    StudyItemDTO itemDetail(StudyItemVo vo);
    StudyItemDTO saveItem(StudyItemVo vo);
    Boolean deleteItem(StudyItemVo vo);
    List<StudyRecordDTO> listRecords(StudyRecordVo vo);
    StudyRecordDTO recordDetail(StudyRecordVo vo);
    StudyRecordDTO saveRecord(StudyRecordVo vo);
    Boolean deleteRecord(StudyRecordVo vo);
    StudyRecordDTO markCorrected(StudyRecordVo vo);
    List<StudyRecordDTO> listCorrections(StudyRecordVo vo);
    Map<String, Object> summary(StudyRecordVo vo);
    Map<String, Object> trend(StudyRecordVo vo);
    Map<String, Object> compare(StudyRecordVo vo);
}
