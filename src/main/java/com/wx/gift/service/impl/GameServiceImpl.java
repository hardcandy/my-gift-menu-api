package com.wx.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.gift.dto.GameDTO;
import com.wx.gift.mapper.FamilyMapper;
import com.wx.gift.mapper.FamilyMemberMapper;
import com.wx.gift.mapper.GameMapper;
import com.wx.gift.mapper.GamePlayLogMapper;
import com.wx.gift.model.Family;
import com.wx.gift.model.FamilyMember;
import com.wx.gift.model.Game;
import com.wx.gift.model.GamePlayLog;
import com.wx.gift.service.GameService;
import com.wx.gift.util.ValidatorUtil;
import com.wx.gift.vo.GameVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {
    @Autowired
    private GameMapper gameMapper;
    @Autowired
    private GamePlayLogMapper gamePlayLogMapper;
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Override
    public List<GameDTO> list(GameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        checkFamilyMember(vo.getFamilyId(), vo.getOpenId());
        return gameMapper.selectList(new LambdaQueryWrapper<Game>()
                        .eq(Game::getFamilyId, vo.getFamilyId())
                        .ne(Game::getStatus, "deleted")
                        .orderByDesc(Game::getLastPlayedAt)
                        .orderByDesc(Game::getId))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public GameDTO detail(GameVo vo) {
        Game game = getOwnedGame(vo);
        return toDto(game);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDTO save(GameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getName(), "游戏名称不能为空");
        ValidatorUtil.checkArgument(vo.getDurationMinutes() != null && vo.getDurationMinutes() > 0, "请填写玩一局需要的时间");
        checkFamilyMember(vo.getFamilyId(), vo.getOpenId());
        Date now = new Date();
        Game game;
        if (vo.getGameId() == null) {
            game = new Game();
            game.setFamilyId(vo.getFamilyId());
            game.setOwnerOpenId(vo.getOpenId());
            game.setStatus("active");
            game.setCreateTime(now);
        } else {
            game = getOwnedGame(vo);
        }
        game.setName(vo.getName());
        game.setImageFileId(StringUtils.defaultString(vo.getImageFileId()));
        game.setLocation(StringUtils.defaultString(vo.getLocation()));
        game.setDurationMinutes(vo.getDurationMinutes());
        game.setModifyTime(now);
        if (game.getId() == null) {
            gameMapper.insert(game);
        } else {
            gameMapper.updateById(game);
        }
        return toDto(gameMapper.selectById(game.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(GameVo vo) {
        Game game = getOwnedGame(vo);
        game.setStatus("deleted");
        game.setModifyTime(new Date());
        gameMapper.updateById(game);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDTO play(GameVo vo) {
        Game game = getOwnedGame(vo);
        ValidatorUtil.checkArgument(vo.getPlayerOpenIds() != null && !vo.getPlayerOpenIds().isEmpty(), "请选择一起玩的人");
        ValidatorUtil.checkArgument(vo.getPlayerNames() != null && !vo.getPlayerNames().isEmpty(), "请选择一起玩的人");
        Date now = new Date();
        String playerOpenIds = join(vo.getPlayerOpenIds());
        String playerNames = join(vo.getPlayerNames());

        GamePlayLog log = new GamePlayLog();
        log.setGameId(game.getId());
        log.setFamilyId(game.getFamilyId());
        log.setOperatorOpenId(vo.getOpenId());
        log.setPlayerOpenIds(playerOpenIds);
        log.setPlayerNames(playerNames);
        log.setSource(StringUtils.defaultIfBlank(vo.getSource(), "manual"));
        log.setPlayedAt(now);
        log.setCreateTime(now);
        gamePlayLogMapper.insert(log);

        game.setLastPlayedAt(now);
        game.setLastPlayedByOpenIds(playerOpenIds);
        game.setLastPlayedByNames(playerNames);
        game.setModifyTime(now);
        gameMapper.updateById(game);
        return toDto(gameMapper.selectById(game.getId()));
    }

    private Game getOwnedGame(GameVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getGameId(), "gameId 不能为空");
        Game game = gameMapper.selectById(vo.getGameId());
        ValidatorUtil.checkArgument(game != null && !"deleted".equals(game.getStatus()), "游戏不存在");
        checkFamilyMember(game.getFamilyId(), vo.getOpenId());
        if (vo.getFamilyId() != null) {
            ValidatorUtil.checkArgument(game.getFamilyId().equals(vo.getFamilyId()), "游戏不在当前圈子");
        }
        return game;
    }

    private void checkFamilyMember(Integer familyId, String openId) {
        Family family = familyMapper.selectById(familyId);
        ValidatorUtil.checkArgument(family != null && !"deleted".equals(family.getStatus()), "圈子不存在");
        if (openId.equals(family.getOwnerOpenId())) {
            return;
        }
        FamilyMember member = familyMemberMapper.selectOne(new LambdaQueryWrapper<FamilyMember>()
                .eq(FamilyMember::getFamilyId, familyId)
                .eq(FamilyMember::getMemberOpenId, openId)
                .last("limit 1"));
        ValidatorUtil.checkNotNull(member, "只有圈内成员可以操作游戏库");
    }

    private GameDTO toDto(Game game) {
        GameDTO dto = new GameDTO();
        dto.setId(game.getId());
        dto.setFamilyId(game.getFamilyId());
        dto.setOwnerOpenId(game.getOwnerOpenId());
        dto.setImageFileId(game.getImageFileId());
        dto.setName(game.getName());
        dto.setLocation(game.getLocation());
        dto.setDurationMinutes(game.getDurationMinutes());
        dto.setLastPlayedAt(game.getLastPlayedAt());
        dto.setLastPlayedByOpenIds(game.getLastPlayedByOpenIds());
        dto.setLastPlayedByNames(game.getLastPlayedByNames());
        dto.setMonthlyPlayCount(monthlyPlayCount(game));
        dto.setCreateTime(game.getCreateTime());
        dto.setModifyTime(game.getModifyTime());
        return dto;
    }

    private Integer monthlyPlayCount(Game game) {
        Date start = Date.from(LocalDate.now(ZoneId.systemDefault())
                .withDayOfMonth(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        return Math.toIntExact(gamePlayLogMapper.selectCount(new LambdaQueryWrapper<GamePlayLog>()
                .eq(GamePlayLog::getGameId, game.getId())
                .ge(GamePlayLog::getPlayedAt, start)));
    }

    private String join(List<String> values) {
        return values.stream()
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.joining(","));
    }
}
