package com.wx.gift.controller;

import com.wx.gift.dto.GameDTO;
import com.wx.gift.service.GameService;
import com.wx.gift.vo.GameVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {
    @Autowired
    private GameService gameService;

    @RequestMapping("/list")
    public List<GameDTO> list(@RequestBody GameVo vo) {
        return gameService.list(vo);
    }

    @RequestMapping("/detail")
    public GameDTO detail(@RequestBody GameVo vo) {
        return gameService.detail(vo);
    }

    @RequestMapping("/save")
    public GameDTO save(@RequestBody GameVo vo) {
        return gameService.save(vo);
    }

    @RequestMapping("/delete")
    public Boolean delete(@RequestBody GameVo vo) {
        return gameService.delete(vo);
    }

    @RequestMapping("/play")
    public GameDTO play(@RequestBody GameVo vo) {
        return gameService.play(vo);
    }
}
