package com.wx.gift.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Builder
@ToString
@Accessors(chain = true)
public class ClientConfigDTO {
    private boolean shareEnabled;
    private String miniProgramPathPrefix;
    private String miniProgramSharePath;
    private boolean rewardEnabled;
    private String rewardImageUrl;
}
