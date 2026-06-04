package com.wx.gift.util;

import com.wx.gift.dto.GiftRuntimeException;
import org.apache.commons.lang3.StringUtils;

public final class ValidatorUtil {
    private ValidatorUtil() {}

    public static void checkArgument(boolean expression, String message) {
        if (!expression) {
            throw new GiftRuntimeException(message);
        }
    }

    public static void checkNotBlank(String value, String message) {
        checkArgument(StringUtils.isNotBlank(value), message);
    }

    public static void checkNotNull(Object value, String message) {
        checkArgument(value != null, message);
    }
}

