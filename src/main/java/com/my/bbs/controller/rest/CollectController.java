package com.my.bbs.controller.rest;

import com.my.bbs.common.Constants;
import com.my.bbs.entity.UserEntity;
import com.my.bbs.service.CollectService;
import com.my.bbs.util.Result;
import com.my.bbs.util.ResultGenerator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CollectController {

    @Resource
    private CollectService collectService;

    @PostMapping("/addCollect/{postId}")
    @ResponseBody
    public Result addCollect(@PathVariable("postId") Long postId,
                             HttpSession httpSession) {
        if (null == postId || postId < 0) {
            return ResultGenerator.genFailResult("postId参数错误");
        }
        UserEntity user = (UserEntity) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        if (collectService.addCollectRecord(user.getUserId(), postId)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }

    @PostMapping("/delCollect/{postId}")
    @ResponseBody
    public Result delCollect(@PathVariable("postId") Long postId,
                             HttpSession httpSession) {
        if (null == postId || postId < 0) {
            return ResultGenerator.genFailResult("postId参数错误");
        }
        UserEntity user = (UserEntity) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        if (collectService.deleteCollectRecord(user.getUserId(), postId)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }
}
