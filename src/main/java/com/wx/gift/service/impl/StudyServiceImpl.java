package com.wx.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.gift.dto.StudyItemDTO;
import com.wx.gift.dto.StudyRecordDTO;
import com.wx.gift.dto.StudySubjectDTO;
import com.wx.gift.mapper.ChildMapper;
import com.wx.gift.mapper.FamilyMapper;
import com.wx.gift.mapper.FamilyMemberMapper;
import com.wx.gift.mapper.StudyItemMapper;
import com.wx.gift.mapper.StudyRecordMapper;
import com.wx.gift.mapper.StudySubjectMapper;
import com.wx.gift.model.Child;
import com.wx.gift.model.Family;
import com.wx.gift.model.FamilyMember;
import com.wx.gift.model.StudyItem;
import com.wx.gift.model.StudyRecord;
import com.wx.gift.model.StudySubject;
import com.wx.gift.service.StudyService;
import com.wx.gift.util.ValidatorUtil;
import com.wx.gift.vo.StudyItemVo;
import com.wx.gift.vo.StudyRecordVo;
import com.wx.gift.vo.StudySubjectVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StudyServiceImpl implements StudyService {
    private static final String DEFAULT_FIELD_CONFIG = "{\"date\":true,\"score\":true,\"hasError\":true,\"errorCount\":true,\"corrected\":true,\"note\":true,\"attachment\":true}";

    @Autowired
    private StudyItemMapper studyItemMapper;
    @Autowired
    private StudyRecordMapper studyRecordMapper;
    @Autowired
    private StudySubjectMapper studySubjectMapper;
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private FamilyMemberMapper familyMemberMapper;
    @Autowired
    private ChildMapper childMapper;

    @Override
    public List<StudySubjectDTO> listSubjects(StudySubjectVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        return studySubjectMapper.selectList(new LambdaQueryWrapper<StudySubject>()
                        .eq(StudySubject::getFamilyId, vo.getFamilyId())
                        .ne(StudySubject::getStatus, "deleted")
                        .orderByAsc(StudySubject::getSortOrder)
                        .orderByDesc(StudySubject::getId))
                .stream().map(this::toSubjectDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudySubjectDTO saveSubject(StudySubjectVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getName(), "学科名称不能为空");
        requireStudyManager(vo.getFamilyId(), vo.getOpenId());
        Date now = new Date();
        StudySubject subject;
        if (vo.getSubjectId() == null) {
            subject = studySubjectMapper.selectOne(new LambdaQueryWrapper<StudySubject>()
                    .eq(StudySubject::getFamilyId, vo.getFamilyId())
                    .eq(StudySubject::getName, vo.getName())
                    .eq(StudySubject::getGradeScope, StringUtils.defaultIfBlank(vo.getGradeScope(), "全部"))
                    .ne(StudySubject::getStatus, "deleted")
                    .last("limit 1"));
            if (subject == null) {
                subject = new StudySubject();
                subject.setFamilyId(vo.getFamilyId());
                subject.setOwnerOpenId(vo.getOpenId());
                subject.setStatus("active");
                subject.setCreateTime(now);
            }
        } else {
            subject = studySubjectMapper.selectById(vo.getSubjectId());
            ValidatorUtil.checkArgument(subject != null && !"deleted".equals(subject.getStatus()), "学科不存在");
            ValidatorUtil.checkArgument(Objects.equals(subject.getFamilyId(), vo.getFamilyId()), "学科不在当前圈子");
        }
        subject.setName(vo.getName());
        subject.setGradeScope(StringUtils.defaultIfBlank(vo.getGradeScope(), "全部"));
        subject.setSortOrder(vo.getSortOrder() == null ? 100 : vo.getSortOrder());
        subject.setModifyTime(now);
        if (subject.getId() == null) studySubjectMapper.insert(subject);
        else studySubjectMapper.updateById(subject);
        return toSubjectDto(studySubjectMapper.selectById(subject.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSubject(StudySubjectVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getSubjectId(), "subjectId 不能为空");
        StudySubject subject = studySubjectMapper.selectById(vo.getSubjectId());
        ValidatorUtil.checkArgument(subject != null && !"deleted".equals(subject.getStatus()), "学科不存在");
        requireStudyManager(subject.getFamilyId(), vo.getOpenId());
        subject.setStatus("deleted");
        subject.setModifyTime(new Date());
        studySubjectMapper.updateById(subject);
        return true;
    }

    @Override
    public List<StudyItemDTO> listItems(StudyItemVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        return studyItemMapper.selectList(new LambdaQueryWrapper<StudyItem>()
                        .eq(StudyItem::getFamilyId, vo.getFamilyId())
                        .ne(StudyItem::getStatus, "deleted")
                        .orderByAsc(StudyItem::getSortOrder)
                        .orderByDesc(StudyItem::getId))
                .stream().map(this::toItemDto).collect(Collectors.toList());
    }

    @Override
    public StudyItemDTO itemDetail(StudyItemVo vo) {
        StudyItem item = requireItem(vo);
        return toItemDto(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudyItemDTO saveItem(StudyItemVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getName(), "学习项名称不能为空");
        requireStudyManager(vo.getFamilyId(), vo.getOpenId());
        Date now = new Date();
        StudyItem item;
        if (vo.getItemId() == null) {
            item = studyItemMapper.selectOne(new LambdaQueryWrapper<StudyItem>()
                    .eq(StudyItem::getFamilyId, vo.getFamilyId())
                    .eq(StudyItem::getName, vo.getName())
                    .eq(StudyItem::getSubjectScope, StringUtils.defaultIfBlank(vo.getSubjectScope(), "全部"))
                    .eq(StudyItem::getGradeScope, StringUtils.defaultIfBlank(vo.getGradeScope(), "全部"))
                    .ne(StudyItem::getStatus, "deleted")
                    .last("limit 1"));
            if (item == null) {
                item = new StudyItem();
                item.setFamilyId(vo.getFamilyId());
                item.setOwnerOpenId(vo.getOpenId());
                item.setStatus("active");
                item.setCreateTime(now);
            }
        } else {
            item = requireItem(vo);
        }
        item.setName(vo.getName());
        item.setSubjectScope(StringUtils.defaultIfBlank(vo.getSubjectScope(), "全部"));
        item.setGradeScope(StringUtils.defaultIfBlank(vo.getGradeScope(), "全部"));
        item.setScoreType(StringUtils.defaultIfBlank(vo.getScoreType(), "text"));
        item.setFieldConfig(StringUtils.defaultIfBlank(vo.getFieldConfig(), DEFAULT_FIELD_CONFIG));
        item.setCorrectionEnabled(vo.getCorrectionEnabled() == null ? 1 : vo.getCorrectionEnabled());
        item.setSortOrder(vo.getSortOrder() == null ? 100 : vo.getSortOrder());
        item.setModifyTime(now);
        if (item.getId() == null) {
            studyItemMapper.insert(item);
        } else {
            studyItemMapper.updateById(item);
        }
        return toItemDto(studyItemMapper.selectById(item.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteItem(StudyItemVo vo) {
        StudyItem item = requireItem(vo);
        requireStudyManager(item.getFamilyId(), vo.getOpenId());
        item.setStatus("deleted");
        item.setModifyTime(new Date());
        studyItemMapper.updateById(item);
        return true;
    }

    @Override
    public List<StudyRecordDTO> listRecords(StudyRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        return queryRecords(vo, false).stream().map(this::toRecordDto).collect(Collectors.toList());
    }

    @Override
    public StudyRecordDTO recordDetail(StudyRecordVo vo) {
        return toRecordDto(requireRecord(vo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudyRecordDTO saveRecord(StudyRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotNull(vo.getChildId(), "请选择孩子");
        ValidatorUtil.checkNotBlank(vo.getGrade(), "请选择年级");
        ValidatorUtil.checkNotBlank(vo.getSubject(), "请选择学科");
        ValidatorUtil.checkNotNull(vo.getItemId(), "请选择学习项");
        requireStudyManager(vo.getFamilyId(), vo.getOpenId());
        Child child = childMapper.selectById(vo.getChildId());
        ValidatorUtil.checkArgument(child != null && Objects.equals(child.getFamilyId(), vo.getFamilyId()), "孩子不在当前圈子");
        StudyItem item = studyItemMapper.selectById(vo.getItemId());
        ValidatorUtil.checkArgument(item != null && Objects.equals(item.getFamilyId(), vo.getFamilyId()) && !"deleted".equals(item.getStatus()), "学习项不存在");
        Date now = new Date();
        StudyRecord record;
        if (vo.getRecordId() == null) {
            record = new StudyRecord();
            record.setFamilyId(vo.getFamilyId());
            record.setCreatedByOpenId(vo.getOpenId());
            record.setStatus("active");
            record.setCreateTime(now);
        } else {
            record = requireRecord(vo);
            ValidatorUtil.checkArgument(Objects.equals(record.getFamilyId(), vo.getFamilyId()), "记录不在当前圈子");
        }
        record.setChildId(child.getId());
        record.setChildName(child.getChildName());
        record.setGrade(vo.getGrade());
        record.setSubject(vo.getSubject());
        record.setItemId(item.getId());
        record.setItemName(item.getName());
        record.setContentTitle(StringUtils.defaultString(vo.getContentTitle()));
        record.setRecordDate(vo.getRecordDate() == null ? now : vo.getRecordDate());
        record.setScoreType(StringUtils.defaultIfBlank(vo.getScoreType(), "text"));
        record.setScoreValue(StringUtils.defaultString(vo.getScoreValue()));
        record.setHasError(vo.getHasError() == null ? 0 : vo.getHasError());
        record.setErrorCount(vo.getErrorCount() == null ? 0 : vo.getErrorCount());
        record.setCorrected(vo.getCorrected() == null ? 1 : vo.getCorrected());
        record.setCorrectionMark(resolveMark(record.getHasError(), record.getCorrected()));
        record.setNote(StringUtils.defaultString(vo.getNote()));
        record.setAttachmentFileId(StringUtils.defaultString(vo.getAttachmentFileId()));
        record.setModifyTime(now);
        if (record.getId() == null) {
            studyRecordMapper.insert(record);
        } else {
            studyRecordMapper.updateById(record);
        }
        return toRecordDto(studyRecordMapper.selectById(record.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRecord(StudyRecordVo vo) {
        StudyRecord record = requireRecord(vo);
        requireStudyManager(record.getFamilyId(), vo.getOpenId());
        record.setStatus("deleted");
        record.setModifyTime(new Date());
        studyRecordMapper.updateById(record);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudyRecordDTO markCorrected(StudyRecordVo vo) {
        StudyRecord record = requireRecord(vo);
        requireStudyManager(record.getFamilyId(), vo.getOpenId());
        record.setHasError(record.getHasError() == null ? 0 : record.getHasError());
        record.setCorrected(1);
        record.setCorrectionMark("★");
        record.setModifyTime(new Date());
        studyRecordMapper.updateById(record);
        return toRecordDto(studyRecordMapper.selectById(record.getId()));
    }

    @Override
    public List<StudyRecordDTO> listCorrections(StudyRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        return queryRecords(vo, true).stream().map(this::toRecordDto).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> summary(StudyRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        List<StudyRecord> records = queryRecords(vo, false);
        long correctionCount = records.stream().filter(this::isUncorrected).count();
        Date weekStart = Date.from(LocalDate.now(ZoneId.systemDefault()).minusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant());
        long weekCount = records.stream().filter(item -> item.getCreateTime() != null && !item.getCreateTime().before(weekStart)).count();
        Map<String, Long> byChild = records.stream().filter(this::isUncorrected)
                .collect(Collectors.groupingBy(StudyRecord::getChildName, LinkedHashMap::new, Collectors.counting()));
        Map<String, Long> bySubject = records.stream().filter(this::isUncorrected)
                .collect(Collectors.groupingBy(StudyRecord::getSubject, LinkedHashMap::new, Collectors.counting()));
        Map<String, Object> result = new HashMap<>();
        result.put("totalRecords", records.size());
        result.put("correctionCount", correctionCount);
        result.put("weekCount", weekCount);
        result.put("byChild", byChild);
        result.put("bySubject", bySubject);
        result.put("latest", records.stream().findFirst().map(this::toRecordDto).orElse(null));
        return result;
    }

    @Override
    public Map<String, Object> trend(StudyRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotNull(vo.getChildId(), "请选择孩子");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        List<StudyRecordDTO> records = queryRecords(vo, false).stream()
                .sorted(Comparator.comparing(StudyRecord::getRecordDate, Comparator.nullsLast(Date::compareTo)))
                .map(this::toRecordDto)
                .collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("averageScore", averageScore(records));
        result.put("correctionCount", records.stream().filter(item -> "△".equals(item.getCorrectionMark())).count());
        return result;
    }

    @Override
    public Map<String, Object> compare(StudyRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotNull(vo.getChildId(), "请选择第一个孩子");
        ValidatorUtil.checkNotNull(vo.getSecondChildId(), "请选择第二个孩子");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(compareChild(vo, vo.getChildId()));
        rows.add(compareChild(vo, vo.getSecondChildId()));
        Map<String, Object> result = new HashMap<>();
        result.put("children", rows);
        return result;
    }

    private List<StudyRecord> queryRecords(StudyRecordVo vo, boolean correctionsOnly) {
        LambdaQueryWrapper<StudyRecord> wrapper = new LambdaQueryWrapper<StudyRecord>()
                .eq(StudyRecord::getFamilyId, vo.getFamilyId())
                .ne(StudyRecord::getStatus, "deleted");
        if (vo.getChildId() != null) wrapper.eq(StudyRecord::getChildId, vo.getChildId());
        if (StringUtils.isNotBlank(vo.getSubject()) && !"全部".equals(vo.getSubject())) wrapper.eq(StudyRecord::getSubject, vo.getSubject());
        if (StringUtils.isNotBlank(vo.getGrade()) && !"全部".equals(vo.getGrade())) wrapper.eq(StudyRecord::getGrade, vo.getGrade());
        if (vo.getItemId() != null) wrapper.eq(StudyRecord::getItemId, vo.getItemId());
        if (correctionsOnly) wrapper.eq(StudyRecord::getCorrectionMark, "△");
        wrapper.orderByDesc(StudyRecord::getCorrectionMark)
                .orderByDesc(StudyRecord::getRecordDate)
                .orderByDesc(StudyRecord::getId);
        return studyRecordMapper.selectList(wrapper);
    }

    private Map<String, Object> compareChild(StudyRecordVo source, Integer childId) {
        StudyRecordVo vo = new StudyRecordVo();
        vo.setOpenId(source.getOpenId());
        vo.setFamilyId(source.getFamilyId());
        vo.setChildId(childId);
        vo.setGrade(source.getGrade());
        vo.setSubject(source.getSubject());
        List<StudyRecordDTO> records = queryRecords(vo, false).stream().map(this::toRecordDto).collect(Collectors.toList());
        Child child = childMapper.selectById(childId);
        Map<String, Object> row = new HashMap<>();
        row.put("childId", childId);
        row.put("childName", child == null ? "孩子" : child.getChildName());
        row.put("recordCount", records.size());
        row.put("averageScore", averageScore(records));
        row.put("correctionCount", records.stream().filter(item -> "△".equals(item.getCorrectionMark())).count());
        row.put("awardCount", records.stream().filter(item -> StringUtils.contains(item.getItemName(), "奖状")).count());
        return row;
    }

    private BigDecimal averageScore(List<StudyRecordDTO> records) {
        List<BigDecimal> scores = records.stream()
                .filter(item -> "score".equals(item.getScoreType()))
                .map(item -> parseDecimal(item.getScoreValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (scores.isEmpty()) return null;
        BigDecimal total = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(scores.size()), 1, RoundingMode.HALF_UP);
    }

    private BigDecimal parseDecimal(String value) {
        try {
            return new BigDecimal(StringUtils.trimToEmpty(value));
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveMark(Integer hasError, Integer corrected) {
        return Integer.valueOf(1).equals(hasError) && !Integer.valueOf(1).equals(corrected) ? "△" : "★";
    }

    private boolean isUncorrected(StudyRecord record) {
        return "△".equals(record.getCorrectionMark());
    }

    private StudyItem requireItem(StudyItemVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getItemId(), "itemId 不能为空");
        StudyItem item = studyItemMapper.selectById(vo.getItemId());
        ValidatorUtil.checkArgument(item != null && !"deleted".equals(item.getStatus()), "学习项不存在");
        requireFamilyMember(item.getFamilyId(), vo.getOpenId());
        if (vo.getFamilyId() != null) ValidatorUtil.checkArgument(Objects.equals(item.getFamilyId(), vo.getFamilyId()), "学习项不在当前圈子");
        return item;
    }

    private StudyRecord requireRecord(StudyRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getRecordId(), "recordId 不能为空");
        StudyRecord record = studyRecordMapper.selectById(vo.getRecordId());
        ValidatorUtil.checkArgument(record != null && !"deleted".equals(record.getStatus()), "学习记录不存在");
        requireFamilyMember(record.getFamilyId(), vo.getOpenId());
        if (vo.getFamilyId() != null) ValidatorUtil.checkArgument(Objects.equals(record.getFamilyId(), vo.getFamilyId()), "学习记录不在当前圈子");
        return record;
    }

    private void ensureDefaultItems(Integer familyId, String openId) {
        Long count = studyItemMapper.selectCount(new LambdaQueryWrapper<StudyItem>()
                .eq(StudyItem::getFamilyId, familyId)
                .ne(StudyItem::getStatus, "deleted"));
        if (count != null && count > 0) return;
        String[] names = {"考试成绩", "形成性练习", "抄词", "单元词语改错", "错题订正", "听写", "口算练习", "奖状", "其他"};
        Date now = new Date();
        for (int i = 0; i < names.length; i++) {
            StudyItem item = new StudyItem();
            item.setFamilyId(familyId);
            item.setOwnerOpenId(openId);
            item.setName(names[i]);
            item.setSubjectScope("全部");
            item.setGradeScope("全部");
            item.setFieldConfig(DEFAULT_FIELD_CONFIG);
            item.setCorrectionEnabled("奖状".equals(names[i]) ? 0 : 1);
            item.setSortOrder((i + 1) * 10);
            item.setStatus("active");
            item.setCreateTime(now);
            item.setModifyTime(now);
            studyItemMapper.insert(item);
        }
    }

    private void requireStudyManager(Integer familyId, String openId) {
        Family family = requireFamilyMember(familyId, openId);
        ValidatorUtil.checkArgument("family".equals(family.getCircleType()), "我的学习仅支持家庭圈");
        String role = circleRole(family, openId);
        ValidatorUtil.checkArgument(isParentRole(role), "只有家长可以维护学习记录");
    }

    private Family requireFamilyMember(Integer familyId, String openId) {
        Family family = familyMapper.selectById(familyId);
        ValidatorUtil.checkArgument(family != null && !"deleted".equals(family.getStatus()), "圈子不存在");
        if (openId.equals(family.getOwnerOpenId())) return family;
        FamilyMember member = familyMemberMapper.selectOne(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, familyId)
                .eq(FamilyMember::getMemberOpenId, openId)
                .last("limit 1"));
        ValidatorUtil.checkNotNull(member, "只有圈内成员可以访问学习记录");
        return family;
    }

    private String circleRole(Family family, String openId) {
        if (openId.equals(family.getOwnerOpenId())) return StringUtils.defaultIfBlank(family.getOwnerRole(), "parent");
        FamilyMember member = familyMemberMapper.selectOne(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, family.getId())
                .eq(FamilyMember::getMemberOpenId, openId)
                .last("limit 1"));
        return member == null ? "relative" : StringUtils.defaultIfBlank(member.getMemberRole(), "relative");
    }

    private boolean isParentRole(String role) {
        return "parent".equals(role)
                || "dad".equals(role)
                || "mom".equals(role)
                || "grandpa".equals(role)
                || "grandma".equals(role)
                || "maternal_grandpa".equals(role)
                || "maternal_grandma".equals(role);
    }

    private StudyItemDTO toItemDto(StudyItem item) {
        StudyItemDTO dto = new StudyItemDTO();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }

    private StudySubjectDTO toSubjectDto(StudySubject subject) {
        StudySubjectDTO dto = new StudySubjectDTO();
        BeanUtils.copyProperties(subject, dto);
        return dto;
    }

    private StudyRecordDTO toRecordDto(StudyRecord record) {
        StudyRecordDTO dto = new StudyRecordDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }
}
