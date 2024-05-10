package com.my.bbs.controller.rest;

import com.my.bbs.common.Constants;
import com.my.bbs.common.ServiceResultEnum;
import com.my.bbs.entity.PostEntity;
import com.my.bbs.entity.UserEntity;
import com.my.bbs.entity.RecentCommentEntity;
import com.my.bbs.service.CollectService;
import com.my.bbs.service.CommentService;
import com.my.bbs.service.PostService;
import com.my.bbs.service.UserService;
import com.my.bbs.util.MD5Util;
import com.my.bbs.util.PatternUtil;
import com.my.bbs.util.Result;
import com.my.bbs.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private CollectService collectService;

    @Resource
    private CommentService commentService;

    @GetMapping({"/login", "/login.html"})
    public String loginPage() {
        return "user/login";
    }

    @GetMapping({"/register", "/register.html"})
    public String registerPage() {
        return "user/reg";
    }

    @GetMapping("/userCenter/{userId}")
    public String userCenterPage(HttpServletRequest request, @PathVariable("userId") Long userId) {
        // 基本用户信息
        UserEntity user = userService.getUserById(userId);
        if (user == null) {
            return "error/error_404";
        }
        // 近期发布的帖子
        List<PostEntity> recentPostList = postService.getRecentPostListByUserId(userId);
        // 近期回复的内容
        List<RecentCommentEntity> recentCommentList = commentService.getRecentCommentListByUserId(userId);

        request.setAttribute("bbsUser", user);
        request.setAttribute("recentPostList", recentPostList);
        request.setAttribute("recentCommentList", recentCommentList);
        return "user/home";
    }

    @GetMapping("/userSet")
    public String userSetPage(HttpServletRequest request) {
        UserEntity user = (UserEntity) request.getSession().getAttribute(Constants.USER_SESSION_KEY);
        request.setAttribute("bbsUser", user);
        return "user/set";
    }

    @GetMapping("/myCenter")
    public String myCenterPage(HttpServletRequest request) {
        // 我的信息
        UserEntity user = (UserEntity) request.getSession().getAttribute(Constants.USER_SESSION_KEY);

        // 我发过的帖子
        List<PostEntity> myPosts = postService.getMyPosts(user.getUserId());
        int myPostCount = 0;
        if (!CollectionUtils.isEmpty(myPosts)) {
            myPostCount = myPosts.size();
        }

        // 我收藏的贴
        List<PostEntity> myCollectPosts = collectService.getCollectRecordsByUserId(user.getUserId());
        int myCollectPostCount = 0;
        if (!CollectionUtils.isEmpty(myCollectPosts)) {
            myCollectPostCount = myCollectPosts.size();
        }

        request.setAttribute("myBBSPostList", myPosts);
        request.setAttribute("myBBSPostCount", myPostCount);
        request.setAttribute("collectRecords", myCollectPosts);
        request.setAttribute("myCollectBBSPostCount", myCollectPostCount);
        request.setAttribute("bbsUser", user);
        return "user/index";
    }

    @PostMapping("/updateUserInfo")
    @ResponseBody
    public Result updateInfo(@RequestParam("nickName") String nickName,
                             @RequestParam("userGender") int userGender,
                             @RequestParam("location") String location,
                             @RequestParam("introduce") String introduce,
                             HttpSession httpSession) {

        if (!StringUtils.hasLength(nickName)) {
            return ResultGenerator.genFailResult("nickName参数错误");
        }
        if (userGender < 1 || userGender > 2) {
            return ResultGenerator.genFailResult("userGender参数错误");
        }
        if (!StringUtils.hasLength(location)) {
            return ResultGenerator.genFailResult("location参数错误");
        }
        if (!StringUtils.hasLength(introduce)) {
            return ResultGenerator.genFailResult("introduce参数错误");
        }
        UserEntity user = (UserEntity) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        user.setNickName(nickName);
        if (userGender == 1) {
            user.setGender("男");
        }
        if (userGender == 2) {
            user.setGender("女");
        }
        user.setLocation(location);
        user.setIntroduce(introduce);
        if (userService.updateUserInfo(user, httpSession)) {
            Result result = ResultGenerator.genSuccessResult();
            return result;
        } else {
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }
    }

    @PostMapping("/updateHeadImg")
    @ResponseBody
    public Result updateHeadImg(@RequestParam("userHeadImg") String userHeadImg,
                                HttpSession httpSession) {

        if (!StringUtils.hasLength(userHeadImg)) {
            return ResultGenerator.genFailResult("userHeadImg参数错误");
        }
        UserEntity user = (UserEntity) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        user.setHeadImgUrl(userHeadImg);
        if (userService.updateUserHeadImg(user, httpSession)) {
            Result result = ResultGenerator.genSuccessResult();
            return result;
        } else {
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }
    }

    @PostMapping("/updatePassword")
    @ResponseBody
    public Result passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword) {
        if (!StringUtils.hasLength(originalPassword) || !StringUtils.hasLength(newPassword)) {
            return ResultGenerator.genFailResult("参数不能为空");
        }
        UserEntity user = (UserEntity) request.getSession().getAttribute(Constants.USER_SESSION_KEY);
        if (userService.updatePassword(user.getUserId(), originalPassword, newPassword)) {
            // 修改成功后清空session中的数据, 需要重新登录
            request.getSession().removeAttribute(Constants.USER_SESSION_KEY);
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute(Constants.USER_SESSION_KEY);
        return "user/login";
    }

    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName") String loginName,
                           @RequestParam("verifyCode") String verifyCode,
                           @RequestParam("nickName") String nickName,
                           @RequestParam("password") String password,
                           HttpSession httpSession) {
        // 检查参数
        if (!StringUtils.hasLength(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (!PatternUtil.isEmail(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NOT_EMAIL.getResult());
        }
        if (!StringUtils.hasLength(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (!StringUtils.hasLength(verifyCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        // 验证码从会话中获取，并进行验证
        String kaptchaCode = httpSession.getAttribute(Constants.VERIFY_CODE_KEY) + "";
        if (!StringUtils.hasLength(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String registerResult = userService.register(loginName, password, nickName);
        // 注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            httpSession.removeAttribute(Constants.VERIFY_CODE_KEY);// 删除session中的验证码
            return ResultGenerator.genSuccessResult();
        }
        // 注册失败
        return ResultGenerator.genFailResult(registerResult);
    }


    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestParam("loginName") String loginName,
                        @RequestParam("verifyCode") String verifyCode,
                        @RequestParam("password") String password,
                        HttpSession httpSession) {
        if (!StringUtils.hasLength(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (!PatternUtil.isEmail(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NOT_EMAIL.getResult());
        }
        if (!StringUtils.hasLength(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (!StringUtils.hasLength(verifyCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
        // 从当前会话中获取验证码，并进行验证
        String kaptchaCode = httpSession.getAttribute(Constants.VERIFY_CODE_KEY) + "";
        if (!StringUtils.hasLength(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String loginResult = userService.login(loginName, MD5Util.MD5Encode(password, "UTF-8"), httpSession);
        // 登录成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(loginResult)) {
            httpSession.removeAttribute(Constants.VERIFY_CODE_KEY);//删除session中的验证码
            return ResultGenerator.genSuccessResult();
        }
        // 登录失败
        return ResultGenerator.genFailResult(loginResult);
    }
}
