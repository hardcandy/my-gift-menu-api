package com.wx.gift.aspect;

import com.wx.gift.config.Constants;
import com.wx.gift.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class ExceptionCustomizer {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<?> exceptionCustomizer(HttpServletRequest req, Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(200).body(Result.builder().code(Constants.FAIL).msg(e.getMessage()).build());
    }
}

