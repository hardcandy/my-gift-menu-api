package com.wx.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.gift.dto.SchulteRecordDTO;
import com.wx.gift.mapper.ChildMapper;
import com.wx.gift.mapper.FamilyMapper;
import com.wx.gift.mapper.FamilyMemberMapper;
import com.wx.gift.mapper.SchulteRecordMapper;
import com.wx.gift.model.Child;
import com.wx.gift.model.Family;
import com.wx.gift.model.FamilyMember;
import com.wx.gift.model.SchulteRecord;
import com.wx.gift.service.MiniGameService;
import com.wx.gift.util.ValidatorUtil;
import com.wx.gift.vo.SchulteRecordVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class MiniGameServiceImpl implements MiniGameService {
    @Autowired
    private SchulteRecordMapper schulteRecordMapper;
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private FamilyMemberMapper familyMemberMapper;
    @Autowired
    private ChildMapper childMapper;

    @Override
    public List<Map<String, Object>> listMiniGames() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> schulte = new LinkedHashMap<>();
        schulte.put("key", "schulte");
        schulte.put("name", "舒尔特方格");
        schulte.put("summary", "训练专注力 / 视觉搜索 / 反应速度");
        schulte.put("recommendedAge", "4岁+");
        schulte.put("difficulties", "2x2 / 3x3 / 4x4 / 5x5 / 6x6");
        list.add(schulte);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SchulteRecordDTO saveSchulteRecord(SchulteRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotNull(vo.getChildId(), "请选择孩子");
        ValidatorUtil.checkNotBlank(vo.getDifficulty(), "请选择难度");
        ValidatorUtil.checkArgument(vo.getGridSize() != null && vo.getGridSize() >= 2 && vo.getGridSize() <= 6, "难度暂不支持");
        ValidatorUtil.checkArgument(vo.getTotalNumbers() != null && vo.getTotalNumbers().equals(vo.getGridSize() * vo.getGridSize()), "数字总数不正确");
        ValidatorUtil.checkArgument(vo.getCompleted() == null || vo.getCompleted() == 1, "未完成记录暂不保存");
        ValidatorUtil.checkArgument(vo.getDurationMs() != null && vo.getDurationMs() > 0, "完成用时不正确");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        Child child = childMapper.selectById(vo.getChildId());
        ValidatorUtil.checkArgument(child != null && Objects.equals(child.getFamilyId(), vo.getFamilyId()), "孩子不在当前圈子");

        Date now = new Date();
        SchulteRecord record = new SchulteRecord();
        record.setFamilyId(vo.getFamilyId());
        record.setChildId(child.getId());
        record.setChildName(child.getChildName());
        record.setChildOpenId(StringUtils.defaultString(child.getChildOpenId()));
        record.setOperatorOpenId(vo.getOpenId());
        record.setGameName("舒尔特方格");
        record.setGameMode("数字顺序");
        record.setDifficulty(vo.getDifficulty());
        record.setGridSize(vo.getGridSize());
        record.setTotalNumbers(vo.getTotalNumbers());
        record.setStartTime(vo.getStartTimestamp() == null ? new Date(now.getTime() - vo.getDurationMs()) : new Date(vo.getStartTimestamp()));
        record.setEndTime(vo.getEndTimestamp() == null ? now : new Date(vo.getEndTimestamp()));
        record.setDurationMs(vo.getDurationMs());
        record.setPauseDurationMs(Math.max(0, vo.getPauseDurationMs() == null ? 0 : vo.getPauseDurationMs()));
        record.setCompleted(1);
        record.setCompletedCount(vo.getCompletedCount() == null ? vo.getTotalNumbers() : vo.getCompletedCount());
        record.setErrorCount(Math.max(0, vo.getErrorCount() == null ? 0 : vo.getErrorCount()));
        record.setAverageIntervalMs(vo.getAverageIntervalMs() == null ? averageInterval(record.getDurationMs(), record.getTotalNumbers()) : vo.getAverageIntervalMs());
        record.setRating(StringUtils.defaultIfBlank(vo.getRating(), rating(record.getErrorCount())));
        record.setNote(StringUtils.defaultString(vo.getNote()));
        record.setStatus("active");
        record.setCreateTime(now);
        record.setModifyTime(now);
        schulteRecordMapper.insert(record);
        return toDto(record);
    }

    @Override
    public List<SchulteRecordDTO> listSchulteRecords(SchulteRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        return queryRecords(vo).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> schulteStats(SchulteRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        List<SchulteRecord> records = queryRecords(vo).stream()
                .sorted(Comparator.comparing(SchulteRecord::getStartTime, Comparator.nullsLast(Date::compareTo)))
                .collect(Collectors.toList());
        List<SchulteRecord> latest = records.stream()
                .sorted(Comparator.comparing(SchulteRecord::getStartTime, Comparator.nullsLast(Date::compareTo)).reversed())
                .limit(resolveLimit(vo.getLimit()))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("records", records.stream().map(this::toDto).collect(Collectors.toList()));
        result.put("latest", latest.stream().map(this::toDto).collect(Collectors.toList()));
        result.put("practiceCount", records.size());
        result.put("bestDurationMs", records.stream().map(SchulteRecord::getDurationMs).filter(Objects::nonNull).min(Integer::compareTo).orElse(null));
        result.put("lastDurationMs", records.isEmpty() ? null : records.get(records.size() - 1).getDurationMs());
        result.put("averageError", average(records.stream().map(SchulteRecord::getErrorCount).collect(Collectors.toList())));
        result.put("tips", buildTips(records));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSchulteRecord(SchulteRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getRecordId(), "recordId 不能为空");
        SchulteRecord record = schulteRecordMapper.selectById(vo.getRecordId());
        ValidatorUtil.checkArgument(record != null && !"deleted".equals(record.getStatus()), "记录不存在");
        requireFamilyMember(record.getFamilyId(), vo.getOpenId());
        ValidatorUtil.checkArgument(Objects.equals(record.getOperatorOpenId(), vo.getOpenId()) || isFamilyOwner(record.getFamilyId(), vo.getOpenId()), "只有创建人或圈主可以删除记录");
        record.setStatus("deleted");
        record.setModifyTime(new Date());
        schulteRecordMapper.updateById(record);
        return true;
    }

    private List<SchulteRecord> queryRecords(SchulteRecordVo vo) {
        LambdaQueryWrapper<SchulteRecord> wrapper = new LambdaQueryWrapper<SchulteRecord>()
                .eq(SchulteRecord::getFamilyId, vo.getFamilyId())
                .ne(SchulteRecord::getStatus, "deleted");
        if (vo.getChildId() != null) wrapper.eq(SchulteRecord::getChildId, vo.getChildId());
        if (StringUtils.isNotBlank(vo.getDifficulty()) && !"全部".equals(vo.getDifficulty())) {
            wrapper.eq(SchulteRecord::getDifficulty, vo.getDifficulty());
        }
        wrapper.orderByDesc(SchulteRecord::getStartTime).orderByDesc(SchulteRecord::getId);
        Integer limit = resolveLimit(vo.getLimit());
        if (limit != null) wrapper.last("limit " + limit);
        return schulteRecordMapper.selectList(wrapper);
    }

    private void requireFamilyMember(Integer familyId, String openId) {
        Family family = familyMapper.selectById(familyId);
        ValidatorUtil.checkArgument(family != null && !"deleted".equals(family.getStatus()), "圈子不存在");
        if (openId.equals(family.getOwnerOpenId())) return;
        FamilyMember member = familyMemberMapper.selectOne(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, familyId)
                .eq(FamilyMember::getMemberOpenId, openId)
                .last("limit 1"));
        ValidatorUtil.checkNotNull(member, "只有圈内成员可以操作小游戏记录");
    }

    private boolean isFamilyOwner(Integer familyId, String openId) {
        Family family = familyMapper.selectById(familyId);
        return family != null && openId.equals(family.getOwnerOpenId());
    }

    private SchulteRecordDTO toDto(SchulteRecord record) {
        SchulteRecordDTO dto = new SchulteRecordDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }

    private Integer resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) return null;
        return Math.min(limit, 100);
    }

    private Integer averageInterval(Integer durationMs, Integer totalNumbers) {
        if (durationMs == null || totalNumbers == null || totalNumbers <= 0) return 0;
        return durationMs / totalNumbers;
    }

    private String rating(Integer errorCount) {
        int count = errorCount == null ? 0 : errorCount;
        if (count <= 1) return "非常专注";
        if (count <= 3) return "继续进步";
        return "完成练习";
    }

    private BigDecimal average(List<Integer> values) {
        List<Integer> valid = values.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (valid.isEmpty()) return BigDecimal.ZERO;
        int sum = valid.stream().mapToInt(Integer::intValue).sum();
        return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(valid.size()), 1, RoundingMode.HALF_UP);
    }

    private List<String> buildTips(List<SchulteRecord> records) {
        List<String> tips = new ArrayList<>();
        if (records.size() < 3) {
            tips.add("先轻松完成几次，后面就能看到自己的进步节奏。");
            return tips;
        }
        List<SchulteRecord> recent = records.subList(Math.max(0, records.size() - 3), records.size());
        List<SchulteRecord> previous = records.subList(Math.max(0, records.size() - 6), Math.max(0, records.size() - 3));
        if (!previous.isEmpty() && avgDuration(recent) < avgDuration(previous)) {
            tips.add("最近几次速度有提升，继续保持。");
        }
        if (!previous.isEmpty() && avgError(recent) < avgError(previous)) {
            tips.add("错误次数减少了，点击更稳定了。");
        }
        if (recent.stream().allMatch(item -> item.getCompleted() != null && item.getCompleted() == 1)) {
            tips.add("最近完成情况很好，练习节奏正在形成。");
        }
        if (tips.isEmpty()) tips.add("状态会有波动，先看准再点击就很好。");
        return tips;
    }

    private double avgDuration(List<SchulteRecord> records) {
        return records.stream().map(SchulteRecord::getDurationMs).filter(Objects::nonNull).mapToInt(Integer::intValue).average().orElse(0);
    }

    private double avgError(List<SchulteRecord> records) {
        return records.stream().map(SchulteRecord::getErrorCount).filter(Objects::nonNull).mapToInt(Integer::intValue).average().orElse(0);
    }
}
