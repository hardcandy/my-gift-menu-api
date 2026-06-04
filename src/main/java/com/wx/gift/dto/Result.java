package com.wx.gift.dto;

import com.wx.gift.config.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code = Constants.SUCCESS;
    private String msg = Constants.SUCCESS_MESSAGE;
    private T data;

    public Result() {}

    public Result(T data) {
        this.data = data;
    }
}

