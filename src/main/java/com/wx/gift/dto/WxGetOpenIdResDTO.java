package com.wx.gift.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WxGetOpenIdResDTO {
    @SerializedName("openid")
    private String openId;
    @SerializedName("session_key")
    private String sessionKey;
    @SerializedName("unionid")
    private String unionId;
    @SerializedName("errcode")
    private Integer errCode;
    @SerializedName("errmsg")
    private String errMsg;
}

