package com.wx.gift.controller;

import com.wx.gift.dto.ClientConfigDTO;
import com.wx.gift.model.BaseUser;
import com.wx.gift.service.UserService;
import com.wx.gift.vo.LoginVo;
import com.wx.gift.vo.ProfileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private UserService userService;

    @Value("${client.miniProgramPathPrefix:}")
    private String miniProgramPathPrefix;

    @Value("${client.miniProgramSharePath:}")
    private String miniProgramSharePath;

    @Value("${client.rewardImageUrl:}")
    private String rewardImageUrl;

    @Value("${client.supportCostItemsJson:}")
    private String supportCostItemsJson;

    @Value("${client.supportersJson:}")
    private String supportersJson;

    @RequestMapping("/userInfoByCode")
    public BaseUser getUserInfo(@RequestBody LoginVo vo) {
        return userService.login(vo);
    }

    @RequestMapping("/profile")
    public BaseUser updateProfile(@RequestBody ProfileVo vo) {
        return userService.updateProfile(vo);
    }

    @RequestMapping("/clientConfig")
    public ClientConfigDTO clientConfig() {
        String prefix = StringUtils.trimWhitespace(miniProgramPathPrefix);
        String sharePath = StringUtils.trimWhitespace(miniProgramSharePath);
        String rewardUrl = StringUtils.trimWhitespace(rewardImageUrl);
        String costItems = StringUtils.trimWhitespace(supportCostItemsJson);
        String supporters = StringUtils.trimWhitespace(supportersJson);
        return ClientConfigDTO.builder()
                .shareEnabled(StringUtils.hasText(prefix))
                .miniProgramPathPrefix(prefix)
                .miniProgramSharePath(sharePath)
                .rewardEnabled(StringUtils.hasText(rewardUrl))
                .rewardImageUrl(rewardUrl)
                .supportCostItemsJson(costItems)
                .supportersJson(supporters)
                .build();
    }
}
