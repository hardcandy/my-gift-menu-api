package com.wx.gift.service.impl;

import com.wx.gift.dto.WxGetOpenIdResDTO;
import com.wx.gift.service.WxService;
import com.wx.gift.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class WxServiceImpl implements WxService {
    @Autowired
    private OkHttpClient okHttpClient;

    @Value("${wx.appid}")
    private String appId;

    @Value("${wx.secret:}")
    private String secret;

    @Value("${wx.host}")
    private String host;

    @Value("${wx.url.getOpenId}")
    private String getOpenIdUrl;

    @Value("${wx.url.subscribeMessageSend}")
    private String subscribeMessageSendUrl;

    private String cachedAccessToken;
    private long cachedAccessTokenExpireAt;

    @Override
    public WxGetOpenIdResDTO getOpenId(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        if (StringUtils.isBlank(secret)) {
            WxGetOpenIdResDTO dto = new WxGetOpenIdResDTO();
            dto.setOpenId("dev_" + code);
            dto.setSessionKey("dev_session");
            return dto;
        }
        Request request = new Request.Builder()
                .url(String.format("%s%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code", host, getOpenIdUrl, appId, secret, code))
                .build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                return null;
            }
            String resBody = Objects.requireNonNull(response.body()).string();
            log.info("getOpenId response: {}", resBody);
            return GsonUtil.fromJson(resBody, WxGetOpenIdResDTO.class);
        } catch (IOException e) {
            log.error("获取 openId 失败", e);
        }
        return null;
    }
    @Override
    public boolean sendSubscribeMessage(String openId, String templateId, String page, String title, String remark) {
        if (StringUtils.isAnyBlank(openId, templateId) || StringUtils.isBlank(secret)) {
            log.info("skip subscribe message, openId/templateId/secret missing. openId={}, templateId={}", openId, templateId);
            return false;
        }
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return false;
        }
        Map<String, Object> body = new HashMap<>();
        body.put("touser", openId);
        body.put("template_id", templateId);
        body.put("page", StringUtils.defaultIfBlank(page, "pages/wishes/wishes"));
        Map<String, Object> data = new HashMap<>();
        data.put("thing1", value(StringUtils.abbreviate(title, 20)));
        data.put("thing2", value(StringUtils.abbreviate(remark, 20)));
        body.put("data", data);
        Request request = new Request.Builder()
                .url(String.format("%s%s?access_token=%s", host, subscribeMessageSendUrl, accessToken))
                .post(RequestBody.create(GsonUtil.toJson(body), MediaType.parse("application/json; charset=utf-8")))
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String resBody = response.body() == null ? "" : response.body().string();
            log.info("sendSubscribeMessage response: {}", resBody);
            return response.isSuccessful() && resBody.contains("\"errcode\":0");
        } catch (IOException e) {
            log.error("发送订阅消息失败", e);
            return false;
        }
    }

    private Map<String, String> value(String value) {
        Map<String, String> item = new HashMap<>();
        item.put("value", StringUtils.defaultString(value));
        return item;
    }

    private String getAccessToken() {
        long now = System.currentTimeMillis();
        if (StringUtils.isNotBlank(cachedAccessToken) && cachedAccessTokenExpireAt > now) {
            return cachedAccessToken;
        }
        Request request = new Request.Builder()
                .url(String.format("%s/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", host, appId, secret))
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }
            String resBody = response.body().string();
            Map result = GsonUtil.fromJson(resBody, Map.class);
            Object token = result.get("access_token");
            if (token == null) {
                log.error("getAccessToken failed: {}", resBody);
                return null;
            }
            cachedAccessToken = String.valueOf(token);
            cachedAccessTokenExpireAt = now + 7000L * 1000;
            return cachedAccessToken;
        } catch (IOException e) {
            log.error("获取 access_token 失败", e);
            return null;
        }
    }

}

