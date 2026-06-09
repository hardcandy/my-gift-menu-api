package com.wx.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.gift.dto.RestaurantDTO;
import com.wx.gift.mapper.FamilyMapper;
import com.wx.gift.mapper.FamilyMemberMapper;
import com.wx.gift.mapper.RestaurantMapper;
import com.wx.gift.mapper.RestaurantScoreMapper;
import com.wx.gift.mapper.RestaurantVisitMapper;
import com.wx.gift.model.Family;
import com.wx.gift.model.FamilyMember;
import com.wx.gift.model.Restaurant;
import com.wx.gift.model.RestaurantScore;
import com.wx.gift.model.RestaurantVisit;
import com.wx.gift.service.RestaurantService;
import com.wx.gift.util.ValidatorUtil;
import com.wx.gift.vo.RestaurantVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    @Autowired
    private RestaurantMapper restaurantMapper;
    @Autowired
    private RestaurantVisitMapper restaurantVisitMapper;
    @Autowired
    private RestaurantScoreMapper restaurantScoreMapper;
    @Autowired
    private FamilyMapper familyMapper;
    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Override
    public List<RestaurantDTO> list(RestaurantVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        checkFamilyMember(vo.getFamilyId(), vo.getOpenId());
        return restaurantMapper.selectList(new LambdaQueryWrapper<Restaurant>()
                        .eq(Restaurant::getFamilyId, vo.getFamilyId())
                        .ne(Restaurant::getStatus, "deleted")
                        .orderByDesc(Restaurant::getAverageScore)
                        .orderByAsc(Restaurant::getDistanceKm)
                        .orderByDesc(Restaurant::getId))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantDTO detail(RestaurantVo vo) {
        return toDto(getOwnedRestaurant(vo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestaurantDTO save(RestaurantVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getFamilyId(), "familyId 不能为空");
        ValidatorUtil.checkNotBlank(vo.getName(), "餐厅名称不能为空");
        checkFamilyMember(vo.getFamilyId(), vo.getOpenId());
        Date now = new Date();
        Restaurant restaurant;
        if (vo.getRestaurantId() == null) {
            restaurant = new Restaurant();
            restaurant.setFamilyId(vo.getFamilyId());
            restaurant.setOwnerOpenId(vo.getOpenId());
            restaurant.setStatus("active");
            restaurant.setCreateTime(now);
        } else {
            restaurant = getOwnedRestaurant(vo);
        }
        restaurant.setImageFileId(StringUtils.defaultString(vo.getImageFileId()));
        restaurant.setName(vo.getName());
        restaurant.setLocation(StringUtils.defaultString(vo.getLocation()));
        restaurant.setAverageCost(defaultMoney(vo.getAverageCost()));
        restaurant.setDistanceKm(defaultDistance(vo.getDistanceKm()));
        restaurant.setRecommendedDishes(StringUtils.defaultString(vo.getRecommendedDishes()));
        restaurant.setCuisineType(StringUtils.defaultString(vo.getCuisineType()));
        restaurant.setTags(StringUtils.defaultString(vo.getTags()));
        restaurant.setModifyTime(now);
        if (restaurant.getId() == null) {
            restaurantMapper.insert(restaurant);
        } else {
            restaurantMapper.updateById(restaurant);
        }
        return toDto(restaurantMapper.selectById(restaurant.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(RestaurantVo vo) {
        Restaurant restaurant = getOwnedRestaurant(vo);
        restaurant.setStatus("deleted");
        restaurant.setModifyTime(new Date());
        restaurantMapper.updateById(restaurant);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestaurantDTO visit(RestaurantVo vo) {
        Restaurant restaurant = getOwnedRestaurant(vo);
        ValidatorUtil.checkArgument(vo.getScores() != null && !vo.getScores().isEmpty(), "请至少填写一个评分");
        Date now = new Date();
        List<RestaurantVo.ScoreVo> scores = vo.getScores().stream()
                .filter(item -> item != null && StringUtils.isNotBlank(item.getScorerOpenId()) && item.getScore() != null)
                .collect(Collectors.toList());
        ValidatorUtil.checkArgument(!scores.isEmpty(), "请至少填写一个评分");
        for (RestaurantVo.ScoreVo score : scores) {
            ValidatorUtil.checkArgument(score.getScore().compareTo(BigDecimal.ONE) >= 0 && score.getScore().compareTo(BigDecimal.TEN) <= 0, "评分需要在 1-10 分之间");
        }

        RestaurantVisit visit = new RestaurantVisit();
        visit.setRestaurantId(restaurant.getId());
        visit.setFamilyId(restaurant.getFamilyId());
        visit.setOperatorOpenId(vo.getOpenId());
        visit.setMemberOpenIds(join(scores.stream().map(RestaurantVo.ScoreVo::getScorerOpenId).collect(Collectors.toList())));
        visit.setMemberNames(join(scores.stream().map(RestaurantVo.ScoreVo::getScorerName).collect(Collectors.toList())));
        visit.setDishes(StringUtils.defaultString(vo.getDishes()));
        visit.setNote(StringUtils.defaultString(vo.getNote()));
        visit.setAteAt(now);
        visit.setCreateTime(now);
        restaurantVisitMapper.insert(visit);

        for (RestaurantVo.ScoreVo scoreVo : scores) {
            RestaurantScore score = new RestaurantScore();
            score.setVisitId(visit.getId());
            score.setRestaurantId(restaurant.getId());
            score.setFamilyId(restaurant.getFamilyId());
            score.setScorerOpenId(scoreVo.getScorerOpenId());
            score.setScorerName(StringUtils.defaultIfBlank(scoreVo.getScorerName(), "圈友"));
            score.setScore(scoreVo.getScore().setScale(1, RoundingMode.HALF_UP));
            score.setNote(StringUtils.defaultString(scoreVo.getNote()));
            score.setCreateTime(now);
            restaurantScoreMapper.insert(score);
        }

        restaurant.setLastAteAt(now);
        restaurant.setAverageScore(averageScore(restaurant.getId()));
        restaurant.setModifyTime(now);
        restaurantMapper.updateById(restaurant);
        return toDto(restaurantMapper.selectById(restaurant.getId()));
    }

    private Restaurant getOwnedRestaurant(RestaurantVo vo) {
        ValidatorUtil.checkNotBlank(vo.getOpenId(), "openId 不能为空");
        ValidatorUtil.checkNotNull(vo.getRestaurantId(), "restaurantId 不能为空");
        Restaurant restaurant = restaurantMapper.selectById(vo.getRestaurantId());
        ValidatorUtil.checkArgument(restaurant != null && !"deleted".equals(restaurant.getStatus()), "餐厅不存在");
        checkFamilyMember(restaurant.getFamilyId(), vo.getOpenId());
        if (vo.getFamilyId() != null) {
            ValidatorUtil.checkArgument(restaurant.getFamilyId().equals(vo.getFamilyId()), "餐厅不在当前圈子");
        }
        return restaurant;
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
        ValidatorUtil.checkNotNull(member, "只有圈内成员可以操作美食之选");
    }

    private RestaurantDTO toDto(Restaurant restaurant) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setFamilyId(restaurant.getFamilyId());
        dto.setOwnerOpenId(restaurant.getOwnerOpenId());
        dto.setImageFileId(restaurant.getImageFileId());
        dto.setName(restaurant.getName());
        dto.setLocation(restaurant.getLocation());
        dto.setAverageCost(restaurant.getAverageCost());
        dto.setDistanceKm(restaurant.getDistanceKm());
        dto.setRecommendedDishes(restaurant.getRecommendedDishes());
        dto.setCuisineType(restaurant.getCuisineType());
        dto.setTags(restaurant.getTags());
        dto.setAverageScore(restaurant.getAverageScore());
        dto.setLastAteAt(restaurant.getLastAteAt());
        dto.setYearlyVisitCount(yearlyVisitCount(restaurant));
        dto.setScoreCount(scoreCount(restaurant));
        dto.setCreateTime(restaurant.getCreateTime());
        dto.setModifyTime(restaurant.getModifyTime());
        return dto;
    }

    private Integer yearlyVisitCount(Restaurant restaurant) {
        Date start = Date.from(LocalDate.now(ZoneId.systemDefault())
                .withDayOfYear(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        return Math.toIntExact(restaurantVisitMapper.selectCount(new LambdaQueryWrapper<RestaurantVisit>()
                .eq(RestaurantVisit::getRestaurantId, restaurant.getId())
                .ge(RestaurantVisit::getAteAt, start)));
    }

    private Integer scoreCount(Restaurant restaurant) {
        return Math.toIntExact(restaurantScoreMapper.selectCount(new LambdaQueryWrapper<RestaurantScore>()
                .eq(RestaurantScore::getRestaurantId, restaurant.getId())));
    }

    private BigDecimal averageScore(Integer restaurantId) {
        List<RestaurantScore> scores = restaurantScoreMapper.selectList(new LambdaQueryWrapper<RestaurantScore>()
                .eq(RestaurantScore::getRestaurantId, restaurantId));
        if (scores.isEmpty()) {
            return null;
        }
        BigDecimal total = scores.stream().map(RestaurantScore::getScore).reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(scores.size()), 1, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultDistance(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private String join(List<String> values) {
        return values.stream()
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.joining(","));
    }
}
