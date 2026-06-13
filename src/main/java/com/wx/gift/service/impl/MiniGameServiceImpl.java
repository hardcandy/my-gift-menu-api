package com.wx.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.gift.dto.SchulteRecordDTO;
import com.wx.gift.mapper.BaseUserMapper;
import com.wx.gift.mapper.ChildMapper;
import com.wx.gift.mapper.FamilyMapper;
import com.wx.gift.mapper.FamilyMemberMapper;
import com.wx.gift.mapper.GomokuGameMapper;
import com.wx.gift.mapper.NimGameMapper;
import com.wx.gift.mapper.RiverCrossingRecordMapper;
import com.wx.gift.mapper.SchulteRecordMapper;
import com.wx.gift.mapper.WordItemMapper;
import com.wx.gift.mapper.WordPackMapper;
import com.wx.gift.mapper.WordPlayRecordMapper;
import com.wx.gift.mapper.WordProgressMapper;
import com.wx.gift.model.BaseUser;
import com.wx.gift.model.Child;
import com.wx.gift.model.Family;
import com.wx.gift.model.FamilyMember;
import com.wx.gift.model.GomokuGame;
import com.wx.gift.model.NimGame;
import com.wx.gift.model.RiverCrossingRecord;
import com.wx.gift.model.SchulteRecord;
import com.wx.gift.model.WordItem;
import com.wx.gift.model.WordPack;
import com.wx.gift.model.WordPlayRecord;
import com.wx.gift.model.WordProgress;
import com.wx.gift.service.MiniGameService;
import com.wx.gift.util.GsonUtil;
import com.wx.gift.util.ValidatorUtil;
import com.wx.gift.vo.GomokuGameVo;
import com.wx.gift.vo.NimGameVo;
import com.wx.gift.vo.RiverCrossingVo;
import com.wx.gift.vo.SchulteRecordVo;
import com.wx.gift.vo.WordDetectiveVo;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class MiniGameServiceImpl implements MiniGameService {
    @Autowired
    private BaseUserMapper baseUserMapper;
    @Autowired
    private SchulteRecordMapper schulteRecordMapper;
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private FamilyMemberMapper familyMemberMapper;
    @Autowired
    private ChildMapper childMapper;
    @Autowired
    private GomokuGameMapper gomokuGameMapper;
    @Autowired
    private NimGameMapper nimGameMapper;
    @Autowired
    private RiverCrossingRecordMapper riverCrossingRecordMapper;
    @Autowired
    private WordPackMapper wordPackMapper;
    @Autowired
    private WordItemMapper wordItemMapper;
    @Autowired
    private WordProgressMapper wordProgressMapper;
    @Autowired
    private WordPlayRecordMapper wordPlayRecordMapper;

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
        Map<String, Object> gomoku = new LinkedHashMap<>();
        gomoku.put("key", "gomoku");
        gomoku.put("name", "五子棋");
        gomoku.put("summary", "双人在线对战 / 房间码加入 / 15x15 棋盘");
        gomoku.put("recommendedAge", "6岁+");
        gomoku.put("difficulties", "黑白轮流落子，先连五子获胜");
        list.add(gomoku);
        Map<String, Object> nim = new LinkedHashMap<>();
        nim.put("key", "nim");
        nim.put("name", "取石子大作战");
        nim.put("summary", "单机练习 / 联网双人 / 随机匹配");
        nim.put("recommendedAge", "6岁+");
        nim.put("difficulties", "21颗石子，拿最后一颗获胜");
        list.add(nim);
        Map<String, Object> word = new LinkedHashMap<>();
        word.put("key", "wordDetective");
        word.put("name", "字词小侦探");
        word.put("summary", "期末字词复习 / 错词本 / 掌握报告");
        word.put("recommendedAge", "小学低年级");
        word.put("difficulties", "听音找字 / 看字选音 / 缺字补全 / 写一写");
        list.add(word);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SchulteRecordDTO saveSchulteRecord(SchulteRecordVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getDifficulty(), "请选择难度");
        ValidatorUtil.checkArgument(vo.getGridSize() != null && vo.getGridSize() >= 2 && vo.getGridSize() <= 6, "难度暂不支持");
        ValidatorUtil.checkArgument(vo.getTotalNumbers() != null && vo.getTotalNumbers().equals(vo.getGridSize() * vo.getGridSize()), "数字总数不正确");
        ValidatorUtil.checkArgument(vo.getCompleted() == null || vo.getCompleted() == 1, "未完成记录暂不保存");
        ValidatorUtil.checkArgument(vo.getDurationMs() != null && vo.getDurationMs() > 0, "完成用时不正确");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());

        Date now = new Date();
        SchulteRecord record = new SchulteRecord();
        record.setFamilyId(vo.getFamilyId());
        fillSchultePlayer(record, vo);
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
        Integer resolvedLimit = resolveLimit(vo.getLimit());
        int latestLimit = resolvedLimit == null ? 20 : resolvedLimit;
        List<SchulteRecord> latest = records.stream()
                .sorted(Comparator.comparing(SchulteRecord::getStartTime, Comparator.nullsLast(Date::compareTo)).reversed())
                .limit(latestLimit)
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createGomokuGame(GomokuGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        Date now = new Date();
        GomokuGame game = new GomokuGame();
        game.setFamilyId(vo.getFamilyId());
        game.setRoomCode(generateGomokuRoomCode());
        game.setBlackOpenId(vo.getOpenId());
        game.setBlackName(userName(vo.getOpenId()));
        game.setCurrentTurn("black");
        game.setBoardText(emptyGomokuBoard());
        game.setMoveCount(0);
        game.setLastMoveIndex(null);
        game.setStatus("waiting");
        game.setCreateTime(now);
        game.setModifyTime(now);
        gomokuGameMapper.insert(game);
        return gomokuDto(game, vo.getOpenId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> joinGomokuGame(GomokuGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getRoomCode(), "请输入房间码");
        GomokuGame game = requireGomokuGame(vo);
        requireFamilyMember(game.getFamilyId(), vo.getOpenId());
        ValidatorUtil.checkArgument(!"canceled".equals(game.getStatus()), "房间已关闭");
        if (vo.getOpenId().equals(game.getBlackOpenId()) || vo.getOpenId().equals(game.getWhiteOpenId())) {
            return gomokuDto(game, vo.getOpenId());
        }
        ValidatorUtil.checkArgument(StringUtils.isBlank(game.getWhiteOpenId()), "房间已经有两位玩家");
        game.setWhiteOpenId(vo.getOpenId());
        game.setWhiteName(userName(vo.getOpenId()));
        game.setStatus("playing");
        game.setModifyTime(new Date());
        gomokuGameMapper.updateById(game);
        return gomokuDto(game, vo.getOpenId());
    }

    @Override
    public Map<String, Object> gomokuGameDetail(GomokuGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        GomokuGame game = requireGomokuGame(vo);
        requireFamilyMember(game.getFamilyId(), vo.getOpenId());
        return gomokuDto(game, vo.getOpenId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> gomokuMove(GomokuGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getRow(), "请选择落子位置");
        ValidatorUtil.checkNotNull(vo.getCol(), "请选择落子位置");
        ValidatorUtil.checkArgument(vo.getRow() >= 0 && vo.getRow() < 15 && vo.getCol() >= 0 && vo.getCol() < 15, "落子位置不正确");
        GomokuGame game = requireGomokuGame(vo);
        requireFamilyMember(game.getFamilyId(), vo.getOpenId());
        ValidatorUtil.checkArgument("playing".equals(game.getStatus()), "棋局还不能落子");
        String color = playerColor(game, vo.getOpenId());
        ValidatorUtil.checkNotBlank(color, "你不是这局的玩家");
        ValidatorUtil.checkArgument(color.equals(game.getCurrentTurn()), "还没轮到你");
        int index = vo.getRow() * 15 + vo.getCol();
        char[] board = StringUtils.defaultIfBlank(game.getBoardText(), emptyGomokuBoard()).toCharArray();
        ValidatorUtil.checkArgument(board[index] == '.', "这里已经有棋子了");
        char stone = "black".equals(color) ? 'B' : 'W';
        board[index] = stone;
        game.setBoardText(new String(board));
        game.setMoveCount((game.getMoveCount() == null ? 0 : game.getMoveCount()) + 1);
        game.setLastMoveIndex(index);
        if (isGomokuWin(board, vo.getRow(), vo.getCol(), stone)) {
            game.setStatus(color + "_win");
            game.setWinnerOpenId(vo.getOpenId());
            game.setWinnerName("black".equals(color) ? game.getBlackName() : game.getWhiteName());
        } else if (game.getMoveCount() >= 225) {
            game.setStatus("draw");
            game.setWinnerOpenId("");
            game.setWinnerName("");
        } else {
            game.setCurrentTurn("black".equals(color) ? "white" : "black");
        }
        game.setModifyTime(new Date());
        gomokuGameMapper.updateById(game);
        return gomokuDto(game, vo.getOpenId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> restartGomokuGame(GomokuGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        GomokuGame game = requireGomokuGame(vo);
        requireFamilyMember(game.getFamilyId(), vo.getOpenId());
        ValidatorUtil.checkArgument(StringUtils.isNotBlank(playerColor(game, vo.getOpenId())), "你不是这局的玩家");
        if (isGomokuFinished(game.getStatus())) {
            Date now = new Date();
            GomokuGame next = new GomokuGame();
            next.setFamilyId(game.getFamilyId());
            next.setRoomCode(game.getRoomCode());
            next.setBlackOpenId(game.getBlackOpenId());
            next.setBlackName(game.getBlackName());
            next.setWhiteOpenId(game.getWhiteOpenId());
            next.setWhiteName(game.getWhiteName());
            next.setCurrentTurn("black");
            next.setBoardText(emptyGomokuBoard());
            next.setMoveCount(0);
            next.setLastMoveIndex(null);
            next.setWinnerOpenId("");
            next.setWinnerName("");
            next.setStatus(StringUtils.isNotBlank(game.getWhiteOpenId()) ? "playing" : "waiting");
            next.setCreateTime(now);
            next.setModifyTime(now);
            gomokuGameMapper.insert(next);
            return gomokuDto(next, vo.getOpenId());
        }
        game.setBoardText(emptyGomokuBoard());
        game.setMoveCount(0);
        game.setLastMoveIndex(null);
        game.setWinnerOpenId("");
        game.setWinnerName("");
        game.setCurrentTurn("black");
        game.setStatus(StringUtils.isNotBlank(game.getWhiteOpenId()) ? "playing" : "waiting");
        game.setModifyTime(new Date());
        gomokuGameMapper.updateById(game);
        return gomokuDto(game, vo.getOpenId());
    }

    @Override
    public List<Map<String, Object>> gomokuLeaderboard(GomokuGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        List<GomokuGame> games = gomokuGameMapper.selectList(new LambdaQueryWrapper<GomokuGame>()
                .eq(GomokuGame::getFamilyId, vo.getFamilyId())
                .in(GomokuGame::getStatus, "black_win", "white_win", "draw")
                .orderByDesc(GomokuGame::getModifyTime));
        Map<String, Map<String, Object>> rows = new LinkedHashMap<>();
        for (GomokuGame game : games) {
            addGomokuPlayer(rows, game.getBlackOpenId(), game.getBlackName());
            addGomokuPlayer(rows, game.getWhiteOpenId(), game.getWhiteName());
            if ("black_win".equals(game.getStatus())) addInt(rows.get(game.getBlackOpenId()), "winCount", 1);
            else if ("white_win".equals(game.getStatus())) addInt(rows.get(game.getWhiteOpenId()), "winCount", 1);
            else {
                addInt(rows.get(game.getBlackOpenId()), "drawCount", 1);
                addInt(rows.get(game.getWhiteOpenId()), "drawCount", 1);
            }
            addInt(rows.get(game.getBlackOpenId()), "playCount", 1);
            addInt(rows.get(game.getWhiteOpenId()), "playCount", 1);
        }
        return rows.values().stream()
                .filter(row -> StringUtils.isNotBlank((String) row.get("openId")))
                .sorted((a, b) -> {
                    int win = ((Integer) b.get("winCount")).compareTo((Integer) a.get("winCount"));
                    if (win != 0) return win;
                    int play = ((Integer) a.get("playCount")).compareTo((Integer) b.get("playCount"));
                    if (play != 0) return play;
                    return ((String) a.get("name")).compareTo((String) b.get("name"));
                })
                .limit(resolveLimit(vo.getLimit()) == null ? 20 : resolveLimit(vo.getLimit()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveRiverCrossingRecord(RiverCrossingVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkArgument(vo.getStepCount() != null && vo.getStepCount() > 0, "通关步数不正确");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        Date now = new Date();
        RiverCrossingRecord record = new RiverCrossingRecord();
        record.setFamilyId(vo.getFamilyId());
        record.setPlayerOpenId(vo.getOpenId());
        record.setPlayerName(userName(vo.getOpenId()));
        record.setStepCount(vo.getStepCount());
        record.setFailCount(Math.max(0, vo.getFailCount() == null ? 0 : vo.getFailCount()));
        record.setHintCount(Math.max(0, vo.getHintCount() == null ? 0 : vo.getHintCount()));
        record.setDurationMs(Math.max(0, vo.getDurationMs() == null ? 0 : vo.getDurationMs()));
        record.setStarCount(Math.max(1, Math.min(3, vo.getStarCount() == null ? 1 : vo.getStarCount())));
        record.setStatus("active");
        record.setCreateTime(now);
        record.setModifyTime(now);
        riverCrossingRecordMapper.insert(record);
        return riverRecordDto(record);
    }

    @Override
    public List<Map<String, Object>> riverCrossingLeaderboard(RiverCrossingVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        Integer limit = resolveLimit(vo.getLimit());
        LambdaQueryWrapper<RiverCrossingRecord> wrapper = new LambdaQueryWrapper<RiverCrossingRecord>()
                .eq(RiverCrossingRecord::getFamilyId, vo.getFamilyId())
                .eq(RiverCrossingRecord::getStatus, "active")
                .orderByDesc(RiverCrossingRecord::getStarCount)
                .orderByAsc(RiverCrossingRecord::getStepCount)
                .orderByAsc(RiverCrossingRecord::getDurationMs)
                .orderByAsc(RiverCrossingRecord::getHintCount)
                .orderByAsc(RiverCrossingRecord::getFailCount);
        if (limit != null) wrapper.last("limit " + limit);
        return riverCrossingRecordMapper.selectList(wrapper).stream().map(this::riverRecordDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createNimGame(NimGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        NimGame game = newNimGame(vo.getFamilyId(), vo.getOpenId(), "ROOM", "WAITING");
        nimGameMapper.insert(game);
        return nimDto(game, vo.getOpenId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> joinNimGame(NimGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getRoomCode(), "请输入房间号");
        NimGame game = requireNimGame(vo);
        requireFamilyMember(game.getFamilyId(), vo.getOpenId());
        ValidatorUtil.checkArgument(!"PLAYING".equals(game.getStatus()), "游戏已开始，无法加入");
        ValidatorUtil.checkArgument(!"FINISHED".equals(game.getStatus()) && !"CLOSED".equals(game.getStatus()), "房间已结束");
        if (vo.getOpenId().equals(game.getHostOpenId()) || vo.getOpenId().equals(game.getGuestOpenId())) {
            return nimDto(game, vo.getOpenId());
        }
        ValidatorUtil.checkArgument(StringUtils.isBlank(game.getGuestOpenId()), "房间已满");
        game.setGuestOpenId(vo.getOpenId());
        game.setGuestName(userName(vo.getOpenId()));
        game.setStatus("READY");
        game.setModifyTime(new Date());
        nimGameMapper.updateById(game);
        return nimDto(game, vo.getOpenId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> randomNimGame(NimGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        closeMyMatchingNim(vo.getFamilyId(), vo.getOpenId());
        NimGame waiting = nimGameMapper.selectOne(new LambdaQueryWrapper<NimGame>()
                .eq(NimGame::getFamilyId, vo.getFamilyId())
                .eq(NimGame::getRoomType, "RANDOM")
                .eq(NimGame::getStatus, "MATCHING")
                .ne(NimGame::getHostOpenId, vo.getOpenId())
                .orderByAsc(NimGame::getCreateTime)
                .last("limit 1"));
        if (waiting == null) {
            NimGame game = newNimGame(vo.getFamilyId(), vo.getOpenId(), "RANDOM", "MATCHING");
            nimGameMapper.insert(game);
            return nimDto(game, vo.getOpenId());
        }
        waiting.setGuestOpenId(vo.getOpenId());
        waiting.setGuestName(userName(vo.getOpenId()));
        startNim(waiting, ThreadLocalRandom.current().nextBoolean() ? waiting.getHostOpenId() : waiting.getGuestOpenId());
        nimGameMapper.updateById(waiting);
        return nimDto(waiting, vo.getOpenId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelNimMatch(NimGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        if (vo.getFamilyId() == null) return true;
        closeMyMatchingNim(vo.getFamilyId(), vo.getOpenId());
        return true;
    }

    @Override
    public Map<String, Object> nimGameDetail(NimGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        NimGame game = requireNimGame(vo);
        requireFamilyMember(game.getFamilyId(), vo.getOpenId());
        return nimDto(game, vo.getOpenId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> startNimGame(NimGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        NimGame game = requireNimGame(vo);
        requireFamilyMember(game.getFamilyId(), vo.getOpenId());
        ValidatorUtil.checkArgument(vo.getOpenId().equals(game.getHostOpenId()), "只有房主可以开始");
        ValidatorUtil.checkArgument(StringUtils.isNotBlank(game.getGuestOpenId()), "需要两位玩家才能开始");
        ValidatorUtil.checkArgument("READY".equals(game.getStatus()) || "WAITING".equals(game.getStatus()), "当前房间不能开始");
        startNim(game, game.getHostOpenId());
        nimGameMapper.updateById(game);
        return nimDto(game, vo.getOpenId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> nimTake(NimGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getTakeCount(), "请选择取几颗");
        NimGame game = requireNimGame(vo);
        requireFamilyMember(game.getFamilyId(), vo.getOpenId());
        ValidatorUtil.checkArgument("PLAYING".equals(game.getStatus()), "游戏未开始");
        ValidatorUtil.checkArgument(vo.getOpenId().equals(game.getHostOpenId()) || vo.getOpenId().equals(game.getGuestOpenId()), "你不是这局玩家");
        ValidatorUtil.checkArgument(vo.getOpenId().equals(game.getCurrentTurnOpenId()), "还没轮到你");
        int take = vo.getTakeCount();
        ValidatorUtil.checkArgument(take >= game.getMinTake() && take <= game.getMaxTake(), "每次只能取 1～3 颗");
        ValidatorUtil.checkArgument(take <= game.getRemainingStones(), "剩余石子不足");
        int remaining = game.getRemainingStones() - take;
        int round = (game.getTotalRounds() == null ? 0 : game.getTotalRounds()) + 1;
        game.setRemainingStones(remaining);
        game.setTotalRounds(round);
        game.setMoveHistory(appendNimMove(game.getMoveHistory(), round, vo.getOpenId(), nimPlayerName(game, vo.getOpenId()), take, remaining));
        if (remaining <= 0) {
            game.setStatus("FINISHED");
            game.setWinnerOpenId(vo.getOpenId());
            game.setWinnerName(nimPlayerName(game, vo.getOpenId()));
            game.setFinishedAt(new Date());
        } else {
            game.setCurrentTurnOpenId(vo.getOpenId().equals(game.getHostOpenId()) ? game.getGuestOpenId() : game.getHostOpenId());
        }
        game.setModifyTime(new Date());
        nimGameMapper.updateById(game);
        return nimDto(game, vo.getOpenId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> restartNimGame(NimGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        NimGame old = requireNimGame(vo);
        requireFamilyMember(old.getFamilyId(), vo.getOpenId());
        ValidatorUtil.checkArgument(vo.getOpenId().equals(old.getHostOpenId()) || vo.getOpenId().equals(old.getGuestOpenId()), "你不是这局玩家");
        NimGame game = newNimGame(old.getFamilyId(), old.getHostOpenId(), old.getRoomType(), StringUtils.isNotBlank(old.getGuestOpenId()) ? "PLAYING" : "WAITING");
        game.setRoomCode(old.getRoomCode());
        game.setGuestOpenId(old.getGuestOpenId());
        game.setGuestName(old.getGuestName());
        if ("PLAYING".equals(game.getStatus())) {
            startNim(game, old.getGuestOpenId());
        }
        nimGameMapper.insert(game);
        return nimDto(game, vo.getOpenId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveNimSoloGame(NimGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        Date now = new Date();
        String computerOpenId = "__nim_computer__";
        boolean computerWin = "computer".equalsIgnoreCase(StringUtils.defaultString(vo.getWinnerType()));
        NimGame game = newNimGame(vo.getFamilyId(), vo.getOpenId(), "SINGLE", "FINISHED");
        game.setGuestOpenId(computerOpenId);
        game.setGuestName("电脑");
        game.setCurrentTurnOpenId("");
        game.setWinnerOpenId(computerWin ? computerOpenId : vo.getOpenId());
        game.setWinnerName(computerWin ? "电脑" : game.getHostName());
        game.setRemainingStones(0);
        game.setTotalRounds(vo.getTotalRounds() == null ? 0 : Math.max(0, vo.getTotalRounds()));
        game.setMoveHistory(StringUtils.defaultIfBlank(vo.getMoveHistory(), "[]"));
        game.setStartedAt(now);
        game.setFinishedAt(now);
        game.setModifyTime(now);
        nimGameMapper.insert(game);
        return nimDto(game, vo.getOpenId());
    }

    @Override
    public List<Map<String, Object>> nimLeaderboard(NimGameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        List<NimGame> games = nimGameMapper.selectList(new LambdaQueryWrapper<NimGame>()
                .eq(NimGame::getFamilyId, vo.getFamilyId())
                .eq(NimGame::getStatus, "FINISHED")
                .orderByDesc(NimGame::getFinishedAt));
        Map<String, Map<String, Object>> rows = new LinkedHashMap<>();
        for (NimGame game : games) {
            addNimPlayer(rows, game.getHostOpenId(), game.getHostName());
            addNimPlayer(rows, game.getGuestOpenId(), game.getGuestName());
            addInt(rows.get(game.getHostOpenId()), "playCount", 1);
            addInt(rows.get(game.getGuestOpenId()), "playCount", 1);
            addInt(rows.get(game.getWinnerOpenId()), "winCount", 1);
        }
        return rows.values().stream()
                .filter(row -> StringUtils.isNotBlank((String) row.get("openId")))
                .sorted((a, b) -> {
                    int win = ((Integer) b.get("winCount")).compareTo((Integer) a.get("winCount"));
                    if (win != 0) return win;
                    return ((Integer) a.get("playCount")).compareTo((Integer) b.get("playCount"));
                })
                .limit(resolveLimit(vo.getLimit()) == null ? 20 : resolveLimit(vo.getLimit()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> listWordPacks(WordDetectiveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        LambdaQueryWrapper<WordPack> wrapper = new LambdaQueryWrapper<WordPack>()
                .eq(WordPack::getFamilyId, vo.getFamilyId())
                .ne(WordPack::getStatus, "deleted");
        if (vo.getChildId() != null) wrapper.and(w -> w.isNull(WordPack::getChildId).or().eq(WordPack::getChildId, vo.getChildId()));
        wrapper.orderByDesc(WordPack::getModifyTime).orderByDesc(WordPack::getId);
        return wordPackMapper.selectList(wrapper).stream().map(this::wordPackDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveWordPack(WordDetectiveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getName(), "词包名称不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        if (vo.getChildId() != null) requireChild(vo.getFamilyId(), vo.getChildId());
        String packName = StringUtils.trim(vo.getName());
        ensureUniqueWordPackName(vo.getFamilyId(), vo.getChildId(), packName, vo.getPackId());
        Date now = new Date();
        WordPack pack;
        if (vo.getPackId() == null) {
            pack = new WordPack();
            pack.setFamilyId(vo.getFamilyId());
            pack.setOwnerOpenId(vo.getOpenId());
            pack.setStatus("active");
            pack.setCreateTime(now);
        } else {
            pack = requireWordPack(vo);
        }
        pack.setChildId(vo.getChildId());
        pack.setName(packName);
        pack.setGrade(StringUtils.defaultIfBlank(vo.getGrade(), "一年级"));
        pack.setSemester(StringUtils.defaultIfBlank(vo.getSemester(), "上学期"));
        pack.setSource(StringUtils.defaultIfBlank(vo.getSource(), "家长自定义"));
        pack.setNote(StringUtils.defaultString(vo.getNote()));
        pack.setModifyTime(now);
        if (pack.getId() == null) wordPackMapper.insert(pack);
        else wordPackMapper.updateById(pack);
        return wordPackDto(wordPackMapper.selectById(pack.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWordPack(WordDetectiveVo vo) {
        WordPack pack = requireWordPack(vo);
        requireFamilyMember(pack.getFamilyId(), vo.getOpenId());
        pack.setStatus("deleted");
        pack.setModifyTime(new Date());
        wordPackMapper.updateById(pack);
        wordItemMapper.selectList(new LambdaQueryWrapper<WordItem>().eq(WordItem::getPackId, pack.getId()).ne(WordItem::getStatus, "deleted"))
                .forEach(item -> {
                    item.setStatus("deleted");
                    item.setModifyTime(new Date());
                    wordItemMapper.updateById(item);
                });
        return true;
    }

    @Override
    public List<Map<String, Object>> listWordItems(WordDetectiveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        LambdaQueryWrapper<WordItem> wrapper = new LambdaQueryWrapper<WordItem>()
                .eq(WordItem::getFamilyId, vo.getFamilyId())
                .ne(WordItem::getStatus, "deleted");
        if (vo.getPackId() != null) wrapper.eq(WordItem::getPackId, vo.getPackId());
        wrapper.orderByDesc(WordItem::getImportant).orderByDesc(WordItem::getId);
        List<WordItem> items = wordItemMapper.selectList(wrapper);
        Map<Integer, WordProgress> progressMap = progressMap(vo.getFamilyId(), vo.getChildId(), items);
        return items.stream().map(item -> wordItemDto(item, progressMap.get(item.getId()))).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveWordItem(WordDetectiveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotNull(vo.getPackId(), "请选择词包");
        ValidatorUtil.checkNotBlank(vo.getWordText(), "字词不能为空");
        WordPack pack = requireWordPack(vo);
        requireFamilyMember(pack.getFamilyId(), vo.getOpenId());
        Date now = new Date();
        WordItem item;
        if (vo.getWordId() == null) {
            item = new WordItem();
            item.setFamilyId(pack.getFamilyId());
            item.setPackId(pack.getId());
            item.setStatus("active");
            item.setCreateTime(now);
        } else {
            item = requireWordItem(vo);
        }
        fillWordItem(item, vo, now);
        if (item.getId() == null) wordItemMapper.insert(item);
        else wordItemMapper.updateById(item);
        return wordItemDto(wordItemMapper.selectById(item.getId()), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, Object>> batchImportWordItems(WordDetectiveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getBatchText(), "请输入要导入的字词");
        WordPack pack = requireWordPack(vo);
        requireFamilyMember(pack.getFamilyId(), vo.getOpenId());
        Date now = new Date();
        List<Map<String, Object>> result = new ArrayList<>();
        for (String line : vo.getBatchText().split("\\r?\\n")) {
            String text = StringUtils.trimToEmpty(line);
            if (StringUtils.isBlank(text)) continue;
            String[] parts = text.split("\\s+", 3);
            WordItem item = new WordItem();
            item.setFamilyId(pack.getFamilyId());
            item.setPackId(pack.getId());
            item.setWordText(parts[0]);
            item.setPinyin(parts.length > 1 ? parts[1] : "");
            item.setPhrases(parts.length > 2 ? parts[2] : "");
            item.setHint(parts.length > 2 ? parts[2] : "");
            item.setMistakeTip("");
            item.setUnitName("");
            item.setNeedRead(1);
            item.setNeedWrite(1);
            item.setImportant(0);
            item.setSource(StringUtils.defaultIfBlank(vo.getSource(), pack.getSource()));
            item.setStatus("active");
            item.setCreateTime(now);
            item.setModifyTime(now);
            wordItemMapper.insert(item);
            result.add(wordItemDto(item, null));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWordItem(WordDetectiveVo vo) {
        WordItem item = requireWordItem(vo);
        requireFamilyMember(item.getFamilyId(), vo.getOpenId());
        item.setStatus("deleted");
        item.setModifyTime(new Date());
        wordItemMapper.updateById(item);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveWordPlayRecord(WordDetectiveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotNull(vo.getChildId(), "请选择孩子");
        ValidatorUtil.checkArgument(vo.getAnswers() != null && !vo.getAnswers().isEmpty(), "没有练习结果");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        Child child = requireChild(vo.getFamilyId(), vo.getChildId());
        WordPack pack = vo.getPackId() == null ? null : wordPackMapper.selectById(vo.getPackId());
        Date now = new Date();
        int correct = 0;
        int wrong = 0;
        int pending = 0;
        for (Map<String, Object> answer : vo.getAnswers()) {
            Integer wordId = toInt(answer.get("wordId"));
            String dimension = StringUtils.defaultIfBlank(String.valueOf(answer.get("dimension")), "recognize");
            String result = StringUtils.defaultIfBlank(String.valueOf(answer.get("result")), "wrong");
            if ("pending".equals(result)) pending++;
            else if ("right".equals(result)) correct++;
            else wrong++;
            updateProgress(vo.getFamilyId(), child.getId(), wordId, dimension, result, now);
        }
        WordPlayRecord record = new WordPlayRecord();
        record.setFamilyId(vo.getFamilyId());
        record.setChildId(child.getId());
        record.setChildName(child.getChildName());
        record.setPackId(pack == null ? null : pack.getId());
        record.setPackName(pack == null ? "" : pack.getName());
        record.setOperatorOpenId(vo.getOpenId());
        record.setMode(StringUtils.defaultIfBlank(vo.getMode(), "comprehensive"));
        record.setTotalCount(vo.getAnswers().size());
        record.setCorrectCount(correct);
        record.setWrongCount(wrong);
        record.setWritePendingCount(pending);
        record.setSummaryJson(GsonUtil.toJson(vo.getAnswers()));
        record.setNote(StringUtils.defaultString(vo.getNote()));
        record.setStatus("active");
        record.setPlayedAt(now);
        record.setCreateTime(now);
        record.setModifyTime(now);
        wordPlayRecordMapper.insert(record);
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", record.getId());
        dto.put("totalCount", record.getTotalCount());
        dto.put("correctCount", correct);
        dto.put("wrongCount", wrong);
        dto.put("writePendingCount", pending);
        dto.put("report", wordReport(vo));
        return dto;
    }

    @Override
    public Map<String, Object> wordReport(WordDetectiveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        List<WordItem> items = wordItemMapper.selectList(new LambdaQueryWrapper<WordItem>()
                .eq(WordItem::getFamilyId, vo.getFamilyId())
                .ne(WordItem::getStatus, "deleted"));
        if (vo.getPackId() != null) items = items.stream().filter(item -> Objects.equals(item.getPackId(), vo.getPackId())).collect(Collectors.toList());
        Map<Integer, WordProgress> progressMap = progressMap(vo.getFamilyId(), vo.getChildId(), items);
        int mastered = 0;
        int basic = 0;
        int review = 0;
        int fresh = 0;
        int weakRead = 0;
        int weakUse = 0;
        int weakWrite = 0;
        int repeated = 0;
        for (WordItem item : items) {
            WordProgress progress = progressMap.get(item.getId());
            String overall = overallStatus(progress);
            if ("mastered".equals(overall)) mastered++;
            else if ("basic".equals(overall)) basic++;
            else if ("review".equals(overall)) review++;
            else fresh++;
            if (progress != null) {
                if (isReview(progress.getReadStatus())) weakRead++;
                if (isReview(progress.getUseStatus())) weakUse++;
                if (isReview(progress.getWriteStatus())) weakWrite++;
                if (maxWrong(progress) >= 3) repeated++;
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalWords", items.size());
        result.put("mastered", mastered);
        result.put("basic", basic);
        result.put("needReview", review);
        result.put("fresh", fresh);
        result.put("masterRate", items.isEmpty() ? 0 : Math.round((mastered + basic * 0.6) * 100 / items.size()));
        result.put("weakRead", weakRead);
        result.put("weakUse", weakUse);
        result.put("weakWrite", weakWrite);
        result.put("repeatedWrong", repeated);
        result.put("tips", reportTips(weakWrite, weakRead, repeated, fresh));
        return result;
    }

    @Override
    public List<Map<String, Object>> wordWrongBook(WordDetectiveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotNull(vo.getChildId(), "请选择孩子");
        requireFamilyMember(vo.getFamilyId(), vo.getOpenId());
        List<WordItem> items = wordItemMapper.selectList(new LambdaQueryWrapper<WordItem>()
                .eq(WordItem::getFamilyId, vo.getFamilyId())
                .ne(WordItem::getStatus, "deleted"));
        if (vo.getPackId() != null) items = items.stream().filter(item -> Objects.equals(item.getPackId(), vo.getPackId())).collect(Collectors.toList());
        Map<Integer, WordProgress> progressMap = progressMap(vo.getFamilyId(), vo.getChildId(), items);
        return items.stream()
                .map(item -> wordItemDto(item, progressMap.get(item.getId())))
                .filter(map -> "review".equals(map.get("overallStatus")) || toInt(map.get("maxWrongCount")) >= 3)
                .collect(Collectors.toList());
    }

    private WordPack requireWordPack(WordDetectiveVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getPackId(), "packId 不能为空");
        WordPack pack = wordPackMapper.selectById(vo.getPackId());
        ValidatorUtil.checkArgument(pack != null && !"deleted".equals(pack.getStatus()), "词包不存在");
        if (vo.getFamilyId() != null) ValidatorUtil.checkArgument(Objects.equals(pack.getFamilyId(), vo.getFamilyId()), "词包不在当前圈子");
        return pack;
    }

    private WordItem requireWordItem(WordDetectiveVo vo) {
        ValidatorUtil.checkNotNull(vo.getWordId(), "wordId 不能为空");
        WordItem item = wordItemMapper.selectById(vo.getWordId());
        ValidatorUtil.checkArgument(item != null && !"deleted".equals(item.getStatus()), "字词不存在");
        if (vo.getFamilyId() != null) ValidatorUtil.checkArgument(Objects.equals(item.getFamilyId(), vo.getFamilyId()), "字词不在当前圈子");
        return item;
    }

    private void ensureUniqueWordPackName(Integer familyId, Integer childId, String name, Integer currentPackId) {
        LambdaQueryWrapper<WordPack> wrapper = new LambdaQueryWrapper<WordPack>()
                .eq(WordPack::getFamilyId, familyId)
                .eq(WordPack::getName, name)
                .ne(WordPack::getStatus, "deleted");
        if (childId == null) wrapper.isNull(WordPack::getChildId);
        else wrapper.eq(WordPack::getChildId, childId);
        if (currentPackId != null) wrapper.ne(WordPack::getId, currentPackId);
        ValidatorUtil.checkArgument(wordPackMapper.selectCount(wrapper) == 0, "词包名称已存在");
    }

    private Child requireChild(Integer familyId, Integer childId) {
        Child child = childMapper.selectById(childId);
        ValidatorUtil.checkArgument(child != null && Objects.equals(child.getFamilyId(), familyId), "孩子不在当前圈子");
        return child;
    }

    private void fillWordItem(WordItem item, WordDetectiveVo vo, Date now) {
        item.setWordText(vo.getWordText());
        item.setPinyin(StringUtils.defaultString(vo.getPinyin()));
        item.setHint(StringUtils.defaultString(vo.getHint()));
        item.setPhrases(StringUtils.defaultString(vo.getPhrases()));
        item.setMistakeTip(StringUtils.defaultString(vo.getMistakeTip()));
        item.setUnitName(StringUtils.defaultString(vo.getUnitName()));
        item.setNeedRead(vo.getNeedRead() == null ? 1 : vo.getNeedRead());
        item.setNeedWrite(vo.getNeedWrite() == null ? 1 : vo.getNeedWrite());
        item.setImportant(vo.getImportant() == null ? 0 : vo.getImportant());
        item.setSource(StringUtils.defaultIfBlank(vo.getSource(), "家长添加"));
        item.setModifyTime(now);
    }

    private Map<String, Object> wordPackDto(WordPack pack) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", pack.getId());
        map.put("familyId", pack.getFamilyId());
        map.put("childId", pack.getChildId());
        map.put("name", pack.getName());
        map.put("grade", pack.getGrade());
        map.put("semester", pack.getSemester());
        map.put("source", pack.getSource());
        map.put("note", pack.getNote());
        map.put("wordCount", wordItemMapper.selectCount(new LambdaQueryWrapper<WordItem>()
                .eq(WordItem::getPackId, pack.getId())
                .ne(WordItem::getStatus, "deleted")));
        map.put("createTime", pack.getCreateTime());
        map.put("modifyTime", pack.getModifyTime());
        return map;
    }

    private Map<String, Object> wordItemDto(WordItem item, WordProgress progress) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", item.getId());
        map.put("familyId", item.getFamilyId());
        map.put("packId", item.getPackId());
        map.put("wordText", item.getWordText());
        map.put("pinyin", item.getPinyin());
        map.put("hint", item.getHint());
        map.put("phrases", item.getPhrases());
        map.put("mistakeTip", item.getMistakeTip());
        map.put("unitName", item.getUnitName());
        map.put("needRead", item.getNeedRead());
        map.put("needWrite", item.getNeedWrite());
        map.put("important", item.getImportant());
        map.put("source", item.getSource());
        map.put("recognizeStatus", progress == null ? "new" : progress.getRecognizeStatus());
        map.put("readStatus", progress == null ? "new" : progress.getReadStatus());
        map.put("useStatus", progress == null ? "new" : progress.getUseStatus());
        map.put("writeStatus", progress == null ? "new" : progress.getWriteStatus());
        map.put("maxWrongCount", progress == null ? 0 : maxWrong(progress));
        map.put("overallStatus", overallStatus(progress));
        map.put("problemText", problemText(progress));
        return map;
    }

    private Map<Integer, WordProgress> progressMap(Integer familyId, Integer childId, List<WordItem> items) {
        if (childId == null || items.isEmpty()) return new HashMap<>();
        List<Integer> ids = items.stream().map(WordItem::getId).collect(Collectors.toList());
        return wordProgressMapper.selectList(new LambdaQueryWrapper<WordProgress>()
                        .eq(WordProgress::getFamilyId, familyId)
                        .eq(WordProgress::getChildId, childId)
                        .in(WordProgress::getWordId, ids)
                        .ne(WordProgress::getStatus, "deleted"))
                .stream().collect(Collectors.toMap(WordProgress::getWordId, item -> item, (a, b) -> a));
    }

    private void updateProgress(Integer familyId, Integer childId, Integer wordId, String dimension, String result, Date now) {
        if (wordId == null) return;
        WordProgress progress = wordProgressMapper.selectOne(new LambdaQueryWrapper<WordProgress>()
                .eq(WordProgress::getChildId, childId)
                .eq(WordProgress::getWordId, wordId)
                .last("limit 1"));
        if (progress == null) {
            progress = new WordProgress();
            progress.setFamilyId(familyId);
            progress.setChildId(childId);
            progress.setWordId(wordId);
            progress.setRecognizeStatus("new");
            progress.setReadStatus("new");
            progress.setUseStatus("new");
            progress.setWriteStatus("new");
            progress.setRecognizeCorrectStreak(0);
            progress.setReadCorrectStreak(0);
            progress.setUseCorrectStreak(0);
            progress.setWriteCorrectStreak(0);
            progress.setRecognizeWrongCount(0);
            progress.setReadWrongCount(0);
            progress.setUseWrongCount(0);
            progress.setWriteWrongCount(0);
            progress.setStatus("active");
            progress.setCreateTime(now);
        }
        applyDimension(progress, dimension, result);
        progress.setLastPracticedAt(now);
        progress.setModifyTime(now);
        if (progress.getId() == null) wordProgressMapper.insert(progress);
        else wordProgressMapper.updateById(progress);
    }

    private void applyDimension(WordProgress progress, String dimension, String result) {
        if ("read".equals(dimension)) {
            progress.setReadCorrectStreak(nextStreak(progress.getReadCorrectStreak(), result));
            progress.setReadWrongCount(nextWrong(progress.getReadWrongCount(), result));
            progress.setReadStatus(nextStatus(progress.getReadCorrectStreak(), progress.getReadWrongCount(), result));
        } else if ("use".equals(dimension)) {
            progress.setUseCorrectStreak(nextStreak(progress.getUseCorrectStreak(), result));
            progress.setUseWrongCount(nextWrong(progress.getUseWrongCount(), result));
            progress.setUseStatus(nextStatus(progress.getUseCorrectStreak(), progress.getUseWrongCount(), result));
        } else if ("write".equals(dimension)) {
            progress.setWriteCorrectStreak(nextStreak(progress.getWriteCorrectStreak(), result));
            progress.setWriteWrongCount(nextWrong(progress.getWriteWrongCount(), result));
            progress.setWriteStatus(nextStatus(progress.getWriteCorrectStreak(), progress.getWriteWrongCount(), result));
        } else {
            progress.setRecognizeCorrectStreak(nextStreak(progress.getRecognizeCorrectStreak(), result));
            progress.setRecognizeWrongCount(nextWrong(progress.getRecognizeWrongCount(), result));
            progress.setRecognizeStatus(nextStatus(progress.getRecognizeCorrectStreak(), progress.getRecognizeWrongCount(), result));
        }
    }

    private Integer nextStreak(Integer streak, String result) {
        if ("pending".equals(result)) return streak == null ? 0 : streak;
        return "right".equals(result) ? (streak == null ? 0 : streak) + 1 : 0;
    }

    private Integer nextWrong(Integer wrong, String result) {
        return "wrong".equals(result) ? (wrong == null ? 0 : wrong) + 1 : (wrong == null ? 0 : wrong);
    }

    private String nextStatus(Integer streak, Integer wrong, String result) {
        if ("pending".equals(result)) return "review";
        if ("wrong".equals(result)) return "review";
        if (wrong != null && wrong >= 3 && (streak == null || streak < 3)) return "review";
        if (streak != null && streak >= 3) return "mastered";
        if (streak != null && streak >= 2) return "basic";
        return "review";
    }

    private String overallStatus(WordProgress progress) {
        if (progress == null) return "new";
        List<String> statuses = new ArrayList<>();
        statuses.add(progress.getRecognizeStatus());
        statuses.add(progress.getReadStatus());
        statuses.add(progress.getUseStatus());
        statuses.add(progress.getWriteStatus());
        if (statuses.stream().anyMatch(this::isReview)) return "review";
        if (statuses.stream().allMatch(item -> "mastered".equals(item))) return "mastered";
        if (statuses.stream().anyMatch(item -> "basic".equals(item) || "mastered".equals(item))) return "basic";
        return "new";
    }

    private boolean isReview(String status) {
        return "review".equals(status);
    }

    private int maxWrong(WordProgress progress) {
        return Math.max(Math.max(safeInt(progress.getRecognizeWrongCount()), safeInt(progress.getReadWrongCount())),
                Math.max(safeInt(progress.getUseWrongCount()), safeInt(progress.getWriteWrongCount())));
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String problemText(WordProgress progress) {
        if (progress == null) return "还没有练习";
        List<String> problems = new ArrayList<>();
        if (isReview(progress.getRecognizeStatus())) problems.add("会认");
        if (isReview(progress.getReadStatus())) problems.add("会读");
        if (isReview(progress.getUseStatus())) problems.add("会用");
        if (isReview(progress.getWriteStatus())) problems.add("会写");
        if (problems.isEmpty()) return "练习状态不错";
        return String.join("、", problems) + "需复习";
    }

    private List<String> reportTips(int weakWrite, int weakRead, int repeated, int fresh) {
        List<String> tips = new ArrayList<>();
        if (weakWrite > 0) tips.add("优先复习不会写的 " + weakWrite + " 个字词。");
        if (repeated > 0) tips.add("反复错的 " + repeated + " 个字词建议今天再过一遍。");
        if (weakRead > 0) tips.add("读音不熟的字词可以先做听音找字。");
        if (fresh > 0) tips.add("还有 " + fresh + " 个字词未练习，适合放进今日闯关。");
        if (tips.isEmpty()) tips.add("整体掌握不错，可以保持轻量复习节奏。");
        return tips;
    }

    private Integer toInt(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return null;
        }
    }

    private List<SchulteRecord> queryRecords(SchulteRecordVo vo) {
        LambdaQueryWrapper<SchulteRecord> wrapper = new LambdaQueryWrapper<SchulteRecord>()
                .eq(SchulteRecord::getFamilyId, vo.getFamilyId())
                .ne(SchulteRecord::getStatus, "deleted");
        if (vo.getChildId() != null) wrapper.eq(SchulteRecord::getChildId, vo.getChildId());
        if (vo.getChildId() == null && StringUtils.isNotBlank(vo.getPlayerType())) {
            wrapper.eq(SchulteRecord::getPlayerType, vo.getPlayerType());
        }
        if (vo.getChildId() == null && StringUtils.isNotBlank(vo.getPlayerOpenId())) {
            wrapper.eq(SchulteRecord::getPlayerOpenId, vo.getPlayerOpenId());
        }
        if (StringUtils.isNotBlank(vo.getDifficulty()) && !"全部".equals(vo.getDifficulty())) {
            wrapper.eq(SchulteRecord::getDifficulty, vo.getDifficulty());
        }
        wrapper.orderByDesc(SchulteRecord::getStartTime).orderByDesc(SchulteRecord::getId);
        Integer limit = resolveLimit(vo.getLimit());
        if (limit != null) wrapper.last("limit " + limit);
        return schulteRecordMapper.selectList(wrapper);
    }

    private String generateGomokuRoomCode() {
        for (int i = 0; i < 20; i++) {
            String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
            Long count = gomokuGameMapper.selectCount(new LambdaQueryWrapper<GomokuGame>()
                    .eq(GomokuGame::getRoomCode, code)
                    .in(GomokuGame::getStatus, "waiting", "playing")
                    .last("limit 1"));
            if (count == null || count == 0) return code;
        }
        return String.valueOf(System.currentTimeMillis()).substring(7, 13);
    }

    private NimGame newNimGame(Integer familyId, String hostOpenId, String roomType, String status) {
        Date now = new Date();
        NimGame game = new NimGame();
        game.setFamilyId(familyId);
        game.setRoomCode(generateNimRoomCode());
        game.setRoomType(roomType);
        game.setHostOpenId(hostOpenId);
        game.setHostName(userName(hostOpenId));
        game.setInitialStones(21);
        game.setMinTake(1);
        game.setMaxTake(3);
        game.setRemainingStones(21);
        game.setTotalRounds(0);
        game.setMoveHistory("[]");
        game.setStatus(status);
        game.setCreateTime(now);
        game.setModifyTime(now);
        return game;
    }

    private String generateNimRoomCode() {
        for (int i = 0; i < 20; i++) {
            String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
            Long count = nimGameMapper.selectCount(new LambdaQueryWrapper<NimGame>()
                    .eq(NimGame::getRoomCode, code)
                    .in(NimGame::getStatus, "WAITING", "MATCHING", "READY", "PLAYING")
                    .last("limit 1"));
            if (count == null || count == 0) return code;
        }
        return String.valueOf(System.currentTimeMillis()).substring(7, 13);
    }

    private void closeMyMatchingNim(Integer familyId, String openId) {
        nimGameMapper.selectList(new LambdaQueryWrapper<NimGame>()
                .eq(NimGame::getFamilyId, familyId)
                .eq(NimGame::getRoomType, "RANDOM")
                .eq(NimGame::getStatus, "MATCHING")
                .eq(NimGame::getHostOpenId, openId))
                .forEach(game -> {
                    game.setStatus("CLOSED");
                    game.setModifyTime(new Date());
                    nimGameMapper.updateById(game);
                });
    }

    private NimGame requireNimGame(NimGameVo vo) {
        NimGame game = null;
        if (vo.getGameId() != null) {
            game = nimGameMapper.selectById(vo.getGameId());
        } else if (StringUtils.isNotBlank(vo.getRoomCode())) {
            game = nimGameMapper.selectOne(new LambdaQueryWrapper<NimGame>()
                    .eq(NimGame::getRoomCode, StringUtils.trim(vo.getRoomCode()))
                    .ne(NimGame::getStatus, "CLOSED")
                    .orderByDesc(NimGame::getId)
                    .last("limit 1"));
        }
        ValidatorUtil.checkNotNull(game, "房间不存在");
        if (vo.getFamilyId() != null) ValidatorUtil.checkArgument(Objects.equals(game.getFamilyId(), vo.getFamilyId()), "房间不在当前圈子");
        return game;
    }

    private void startNim(NimGame game, String firstOpenId) {
        Date now = new Date();
        game.setStatus("PLAYING");
        game.setRemainingStones(game.getInitialStones());
        game.setTotalRounds(0);
        game.setMoveHistory("[]");
        game.setCurrentTurnOpenId(firstOpenId);
        game.setWinnerOpenId("");
        game.setWinnerName("");
        game.setStartedAt(now);
        game.setFinishedAt(null);
        game.setModifyTime(now);
    }

    @SuppressWarnings("unchecked")
    private String appendNimMove(String historyText, int round, String openId, String name, int take, int remaining) {
        List<Map<String, Object>> moves;
        try {
            moves = GsonUtil.fromJson(StringUtils.defaultIfBlank(historyText, "[]"), List.class);
        } catch (Exception e) {
            moves = new ArrayList<>();
        }
        Map<String, Object> move = new LinkedHashMap<>();
        move.put("round", round);
        move.put("openId", openId);
        move.put("name", name);
        move.put("takeCount", take);
        move.put("remainingStones", remaining);
        move.put("createdAt", System.currentTimeMillis());
        moves.add(move);
        return GsonUtil.toJson(moves);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> nimMoves(String historyText) {
        try {
            return GsonUtil.fromJson(StringUtils.defaultIfBlank(historyText, "[]"), List.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String nimPlayerName(NimGame game, String openId) {
        if (openId != null && openId.equals(game.getHostOpenId())) return game.getHostName();
        if (openId != null && openId.equals(game.getGuestOpenId())) return game.getGuestName();
        return "玩家";
    }

    private Map<String, Object> nimDto(NimGame game, String openId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", game.getId());
        map.put("familyId", game.getFamilyId());
        map.put("roomCode", game.getRoomCode());
        map.put("roomType", game.getRoomType());
        map.put("hostOpenId", game.getHostOpenId());
        map.put("hostName", game.getHostName());
        map.put("guestOpenId", StringUtils.defaultString(game.getGuestOpenId()));
        map.put("guestName", StringUtils.defaultString(game.getGuestName()));
        map.put("currentTurnOpenId", StringUtils.defaultString(game.getCurrentTurnOpenId()));
        map.put("winnerOpenId", StringUtils.defaultString(game.getWinnerOpenId()));
        map.put("winnerName", StringUtils.defaultString(game.getWinnerName()));
        map.put("initialStones", game.getInitialStones());
        map.put("minTake", game.getMinTake());
        map.put("maxTake", game.getMaxTake());
        map.put("remainingStones", game.getRemainingStones());
        map.put("totalRounds", game.getTotalRounds());
        map.put("moveHistory", nimMoves(game.getMoveHistory()));
        map.put("status", game.getStatus());
        map.put("isHost", openId != null && openId.equals(game.getHostOpenId()));
        map.put("isPlayer", openId != null && (openId.equals(game.getHostOpenId()) || openId.equals(game.getGuestOpenId())));
        map.put("myTurn", "PLAYING".equals(game.getStatus()) && openId != null && openId.equals(game.getCurrentTurnOpenId()));
        map.put("matched", "PLAYING".equals(game.getStatus()) || "READY".equals(game.getStatus()));
        map.put("createTime", game.getCreateTime());
        map.put("modifyTime", game.getModifyTime());
        map.put("startedAt", game.getStartedAt());
        map.put("finishedAt", game.getFinishedAt());
        return map;
    }

    private void addNimPlayer(Map<String, Map<String, Object>> rows, String openId, String name) {
        if (StringUtils.isBlank(openId)) return;
        if ("__nim_computer__".equals(openId)) return;
        rows.computeIfAbsent(openId, key -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("openId", key);
            row.put("name", StringUtils.defaultIfBlank(name, "玩家"));
            row.put("playCount", 0);
            row.put("winCount", 0);
            return row;
        });
    }

    private GomokuGame requireGomokuGame(GomokuGameVo vo) {
        GomokuGame game = null;
        if (vo.getGameId() != null) {
            game = gomokuGameMapper.selectById(vo.getGameId());
        } else if (StringUtils.isNotBlank(vo.getRoomCode())) {
            game = gomokuGameMapper.selectOne(new LambdaQueryWrapper<GomokuGame>()
                    .eq(GomokuGame::getRoomCode, StringUtils.trim(vo.getRoomCode()))
                    .ne(GomokuGame::getStatus, "deleted")
                    .orderByDesc(GomokuGame::getId)
                    .last("limit 1"));
        }
        ValidatorUtil.checkArgument(game != null && !"deleted".equals(game.getStatus()), "棋局不存在");
        if (vo.getFamilyId() != null) {
            ValidatorUtil.checkArgument(Objects.equals(game.getFamilyId(), vo.getFamilyId()), "棋局不在当前圈子");
        }
        return game;
    }

    private String emptyGomokuBoard() {
        StringBuilder builder = new StringBuilder(225);
        for (int i = 0; i < 225; i++) builder.append('.');
        return builder.toString();
    }

    private String playerColor(GomokuGame game, String openId) {
        if (openId != null && openId.equals(game.getBlackOpenId())) return "black";
        if (openId != null && openId.equals(game.getWhiteOpenId())) return "white";
        return "";
    }

    private void addGomokuPlayer(Map<String, Map<String, Object>> rows, String openId, String name) {
        if (StringUtils.isBlank(openId)) return;
        rows.computeIfAbsent(openId, key -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("openId", key);
            row.put("name", StringUtils.defaultIfBlank(name, "棋手"));
            row.put("playCount", 0);
            row.put("winCount", 0);
            row.put("drawCount", 0);
            return row;
        });
    }

    private void addInt(Map<String, Object> row, String key, int value) {
        if (row == null) return;
        row.put(key, ((Integer) row.get(key)) + value);
    }

    private boolean isGomokuFinished(String status) {
        return "black_win".equals(status) || "white_win".equals(status) || "draw".equals(status);
    }

    private String userName(String openId) {
        BaseUser user = baseUserMapper.selectOne(new LambdaQueryWrapper<BaseUser>()
                .eq(BaseUser::getOpenId, openId)
                .last("limit 1"));
        return user == null ? "棋手" : StringUtils.defaultIfBlank(user.getNickName(), "棋手");
    }

    private Map<String, Object> gomokuDto(GomokuGame game, String openId) {
        Map<String, Object> map = new LinkedHashMap<>();
        String myColor = playerColor(game, openId);
        map.put("id", game.getId());
        map.put("familyId", game.getFamilyId());
        map.put("roomCode", game.getRoomCode());
        map.put("blackOpenId", game.getBlackOpenId());
        map.put("blackName", game.getBlackName());
        map.put("whiteOpenId", StringUtils.defaultString(game.getWhiteOpenId()));
        map.put("whiteName", StringUtils.defaultString(game.getWhiteName()));
        map.put("currentTurn", game.getCurrentTurn());
        map.put("boardText", StringUtils.defaultIfBlank(game.getBoardText(), emptyGomokuBoard()));
        map.put("moveCount", game.getMoveCount() == null ? 0 : game.getMoveCount());
        map.put("lastMoveIndex", game.getLastMoveIndex());
        map.put("winnerOpenId", StringUtils.defaultString(game.getWinnerOpenId()));
        map.put("winnerName", StringUtils.defaultString(game.getWinnerName()));
        map.put("status", game.getStatus());
        map.put("myColor", myColor);
        map.put("myTurn", "playing".equals(game.getStatus()) && StringUtils.isNotBlank(myColor) && myColor.equals(game.getCurrentTurn()));
        map.put("createTime", game.getCreateTime());
        map.put("modifyTime", game.getModifyTime());
        return map;
    }

    private Map<String, Object> riverRecordDto(RiverCrossingRecord record) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", record.getId());
        map.put("familyId", record.getFamilyId());
        map.put("playerOpenId", record.getPlayerOpenId());
        map.put("playerName", record.getPlayerName());
        map.put("stepCount", record.getStepCount());
        map.put("failCount", record.getFailCount());
        map.put("hintCount", record.getHintCount());
        map.put("durationMs", record.getDurationMs());
        map.put("starCount", record.getStarCount());
        map.put("createTime", record.getCreateTime());
        return map;
    }

    private boolean isGomokuWin(char[] board, int row, int col, char stone) {
        int[][] directions = new int[][]{{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        for (int[] direction : directions) {
            int count = 1
                    + countGomokuLine(board, row, col, direction[0], direction[1], stone)
                    + countGomokuLine(board, row, col, -direction[0], -direction[1], stone);
            if (count >= 5) return true;
        }
        return false;
    }

    private int countGomokuLine(char[] board, int row, int col, int rowStep, int colStep, char stone) {
        int count = 0;
        int r = row + rowStep;
        int c = col + colStep;
        while (r >= 0 && r < 15 && c >= 0 && c < 15 && board[r * 15 + c] == stone) {
            count++;
            r += rowStep;
            c += colStep;
        }
        return count;
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

    private void fillSchultePlayer(SchulteRecord record, SchulteRecordVo vo) {
        if (vo.getChildId() != null || StringUtils.isBlank(vo.getPlayerType()) || "child".equals(vo.getPlayerType())) {
            ValidatorUtil.checkNotNull(vo.getChildId(), "请选择记录对象");
            Child child = childMapper.selectById(vo.getChildId());
            ValidatorUtil.checkArgument(child != null && Objects.equals(child.getFamilyId(), vo.getFamilyId()), "孩子不在当前圈子");
            record.setChildId(child.getId());
            record.setChildName(child.getChildName());
            record.setChildOpenId(StringUtils.defaultString(child.getChildOpenId()));
            record.setPlayerType("child");
            record.setPlayerOpenId(StringUtils.defaultString(child.getChildOpenId()));
            record.setPlayerName(child.getChildName());
            return;
        }

        ValidatorUtil.checkArgument("adult".equals(vo.getPlayerType()), "记录对象类型不正确");
        ValidatorUtil.checkNotBlank(vo.getPlayerOpenId(), "请选择记录对象");
        requireFamilyMember(vo.getFamilyId(), vo.getPlayerOpenId());
        String playerName = StringUtils.defaultIfBlank(vo.getPlayerName(), "圈友");
        record.setChildId(null);
        record.setChildName(playerName);
        record.setChildOpenId(vo.getPlayerOpenId());
        record.setPlayerType("adult");
        record.setPlayerOpenId(vo.getPlayerOpenId());
        record.setPlayerName(playerName);
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
