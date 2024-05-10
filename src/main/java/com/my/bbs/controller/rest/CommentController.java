package com.my.bbs.controller.rest;

import com.my.bbs.common.Constants;
import com.my.bbs.common.ServiceResultEnum;
import com.my.bbs.entity.CommentEntity;
import com.my.bbs.entity.UserEntity;
import com.my.bbs.service.CommentService;
import com.my.bbs.util.Result;
import com.my.bbs.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

@Controller
public class CommentController {

    @Resource
    private CommentService commentService;

    @PostMapping("/replyPost")
    @ResponseBody
    public Result replyPost(@RequestParam("postId") Long postId,
                            @RequestParam(value = "parentCommentUserId", required = false) Long parentCommentUserId,
                            @RequestParam("commentBody") String commentBody,
                            @RequestParam("verifyCode") String verifyCode,
                            HttpSession httpSession) {
        if (null == postId || postId < 0) {
            return ResultGenerator.genFailResult("postId参数错误");
        }
        if (!StringUtils.hasLength(commentBody)) {
            return ResultGenerator.genFailResult("commentBody参数错误");
        }
        if (commentBody.trim().length() > 200) {
            return ResultGenerator.genFailResult("评论内容过长");
        }
        String kaptchaCode = httpSession.getAttribute(Constants.VERIFY_CODE_KEY) + "";
        if (!StringUtils.hasLength(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        UserEntity user = (UserEntity) httpSession.getAttribute(Constants.USER_SESSION_KEY);

        CommentEntity newComment = new CommentEntity();
        newComment.setCommentBody(commentBody);
        newComment.setCommentUserId(user.getUserId());
        newComment.setParentCommentUserId(parentCommentUserId);
        newComment.setPostId(postId);

        if (commentService.addPostComment(newComment)) {
            httpSession.removeAttribute(Constants.VERIFY_CODE_KEY);
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }


    @PostMapping("/delReply/{commentId}")
    @ResponseBody
    public Result delReply(@PathVariable("commentId") Long commentId,
                           HttpSession httpSession) {
        if (null == commentId || commentId < 0) {
            return ResultGenerator.genFailResult("commentId参数错误");
        }

        UserEntity bbsUser = (UserEntity) httpSession.getAttribute(Constants.USER_SESSION_KEY);

        if (commentService.delPostComment(commentId,bbsUser.getUserId())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }
}
