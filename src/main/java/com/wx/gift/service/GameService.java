package com.wx.gift.service;

import com.wx.gift.dto.GameDTO;
import com.wx.gift.vo.GameVo;

import java.util.List;

public interface GameService {
    List<GameDTO> list(GameVo vo);
    GameDTO detail(GameVo vo);
    GameDTO save(GameVo vo);
    Boolean delete(GameVo vo);
    GameDTO play(GameVo vo);
}
