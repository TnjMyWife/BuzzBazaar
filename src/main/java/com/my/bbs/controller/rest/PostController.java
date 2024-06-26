package com.my.bbs.controller.rest;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.my.bbs.common.Constants;
import com.my.bbs.common.ServiceResultEnum;
import com.my.bbs.entity.PostEntity;
import com.my.bbs.entity.CategoryEntity;
import com.my.bbs.entity.UserEntity;
import com.my.bbs.service.*;
import com.my.bbs.util.PageResult;
import com.my.bbs.util.Result;
import com.my.bbs.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PostController {
    @Resource
    private PostService postService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private UserService userService;
    @Resource
    private CollectService collectService;
    @Resource
    private CommentService commentService;

    @GetMapping("detail/{postId}")
    public String postDetail(HttpServletRequest request, @PathVariable(value = "postId") Long postId,
                             @RequestParam(value = "commentPage", required = false, defaultValue = "1") Integer commentPage) {
        // 分类信息
        List<CategoryEntity> categoryEntities = categoryService.getBBSPostCategories();
        if (CollectionUtils.isEmpty(categoryEntities)) {
            return "error/error_404";
        }
        // 帖子信息
        PostEntity bbsPost = postService.getPostForDetail(postId);
        if (bbsPost == null) {
            return "error/error_404";
        }
        // 发帖用户信息
        UserEntity postUser = userService.getUserById(bbsPost.getPublishUserId());
        if (postUser == null) {
            return "error/error_404";
        }
        // 当前用户信息
        UserEntity currentUser = (UserEntity) request.getSession().getAttribute(Constants.USER_SESSION_KEY);
        // 评论数据
        PageResult commentsPage = commentService.getCommentsByPostId(postId, commentPage);
        // 设置属性，传递数据
        request.setAttribute("bbsPostCategories", categoryEntities);
        request.setAttribute("bbsPost", bbsPost);
        request.setAttribute("bbsUser", postUser);
        request.setAttribute("currentUserCollectFlag", collectService.validUserCollect(currentUser.getUserId(), postId));
        request.setAttribute("hotTopicBBSPostList", postService.getHotPosts());
        request.setAttribute("commentsPage", commentsPage);

        return "post/detail";
    }

    @GetMapping("editPostPage/{postId}")
    public String editPostPage(HttpServletRequest request, @PathVariable(value = "postId") Long postId) {
        UserEntity user = (UserEntity) request.getSession().getAttribute(Constants.USER_SESSION_KEY);
        List<CategoryEntity> categoryEntities = categoryService.getBBSPostCategories();
        if (CollectionUtils.isEmpty(categoryEntities)) {
            return "error/error_404";
        }

        if (null == postId || postId < 0) {
            return "error/error_404";
        }
        PostEntity post = postService.getPostById(postId);
        if (post == null) {
            return "error/error_404";
        }
        if (!user.getUserId().equals(post.getPublishUserId())) {
            request.setAttribute("message", "无权修改他人帖子");
            return "error/error";
        }

        request.setAttribute("bbsPostCategories", categoryEntities);
        request.setAttribute("bbsPost", post);
        request.setAttribute("postId", postId);

        return "post/edit";
    }

    @GetMapping("/addPostPage")
    public String addPostPage(HttpServletRequest request) {
        List<CategoryEntity> categoryEntities = categoryService.getBBSPostCategories();
        if (CollectionUtils.isEmpty(categoryEntities)) {
            return "error/error_404";
        }

        request.setAttribute("bbsPostCategories", categoryEntities);
        return "post/add";
    }

    @PostMapping("/addPost")
    @ResponseBody
    public Result addPost(@RequestParam("postTitle") String postTitle,
                          @RequestParam("postCategoryId") Integer postCategoryId,
                          @RequestParam("postContent") String postContent,
                          @RequestParam("verifyCode") String verifyCode,
                          HttpSession httpSession) {
        if (!StringUtils.hasLength(postTitle)) {
            return ResultGenerator.genFailResult("postTitle参数错误");
        }
        if (null == postCategoryId || postCategoryId < 0) {
            return ResultGenerator.genFailResult("postCategoryId参数错误");
        }
        CategoryEntity category = categoryService.selectById(postCategoryId);
        if (null == category) {
            return ResultGenerator.genFailResult("帖子类别参数错误");
        }
        if (!StringUtils.hasLength(postContent)) {
            return ResultGenerator.genFailResult("postContent参数错误");
        }
        if (postTitle.trim().length() > 32) {
            return ResultGenerator.genFailResult("审核未通过。原因：标题过长");
        }
        List<String> sentitives = SensitiveWordHelper.findAll(postTitle);
        if (!sentitives.isEmpty()) {
            String joinedSentitives = sentitives.stream()
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.joining(", "));
            return ResultGenerator.genFailResult("审核未通过。原因：标题包含敏感内容。" + joinedSentitives);
        }
        if (postContent.trim().length() > 100000) {
            return ResultGenerator.genFailResult("审核未通过。原因：内容过长");
        }
        sentitives = SensitiveWordHelper.findAll(postContent);
        if (!sentitives.isEmpty()) {
            String joinedSentitives = sentitives.stream()
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.joining(", "));
            return ResultGenerator.genFailResult("审核未通过。原因：帖子包含敏感内容。" + joinedSentitives);
        }

        String kaptchaCode = httpSession.getAttribute(Constants.VERIFY_CODE_KEY) + "";
        if (!StringUtils.hasLength(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        UserEntity user = (UserEntity) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        PostEntity post = new PostEntity();
        post.setPublishUserId(user.getUserId());
        post.setPostTitle(postTitle);
        post.setPostContent(postContent);
        post.setPostCategoryId(postCategoryId);
        post.setPostCategoryName(category.getCategoryName());
        if (postService.savePost(post)) {
            httpSession.removeAttribute(Constants.VERIFY_CODE_KEY);
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }

    @PostMapping("/delPost/{postId}")
    @ResponseBody
    public Result delPost(@PathVariable("postId") Long postId,
                          HttpSession httpSession) {
        if (null == postId || postId < 0) {
            return ResultGenerator.genFailResult("postId参数错误");
        }
        UserEntity user = (UserEntity) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        if (postService.deletePost(user.getUserId(), postId)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }

    @PostMapping("/likePost/{postId}")
    @ResponseBody
    public Result likePost(@PathVariable("postId") Long postId) {
        PostEntity post = postService.getPostById(postId);
        if (post == null) {
            return ResultGenerator.genFailResult("postId参数错误");
        }
        post.setPostLikes(post.getPostLikes()+1);
        if (postService.updatePost(post)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }

    @PostMapping("/sharePost/{postId}")
    @ResponseBody
    public Result sharePost(@PathVariable("postId") Long postId) {
        PostEntity post = postService.getPostById(postId);
        if (post == null) {
            return ResultGenerator.genFailResult("postId参数错误");
        }
        post.setPostShares(post.getPostShares()+1);
        if (postService.updatePost(post)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }

    @PostMapping("/editPost")
    @ResponseBody
    public Result editPost(@RequestParam("postId") Long postId,
                           @RequestParam("postTitle") String postTitle,
                           @RequestParam("postCategoryId") Integer postCategoryId,
                           @RequestParam("postContent") String postContent,
                           @RequestParam("verifyCode") String verifyCode,
                           HttpSession httpSession) {
        UserEntity user = (UserEntity) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        if (null == postId || postId < 0) {
            return ResultGenerator.genFailResult("postId参数错误");
        }
        PostEntity post = postService.getPostById(postId);
        if (post == null) {
            return ResultGenerator.genFailResult("postId参数错误");
        }
        if (!user.getUserId().equals(post.getPublishUserId())) {
            return ResultGenerator.genFailResult("无权限修改他人帖子");
        }
        if (!StringUtils.hasLength(postTitle)) {
            return ResultGenerator.genFailResult("postTitle参数错误");
        }
        if (null == postCategoryId || postCategoryId < 0) {
            return ResultGenerator.genFailResult("postCategoryId参数错误");
        }
        CategoryEntity bbsPostCategory = categoryService.selectById(postCategoryId);
        if (null == bbsPostCategory) {
            return ResultGenerator.genFailResult("postCategoryId参数错误");
        }
        if (!StringUtils.hasLength(postContent)) {
            return ResultGenerator.genFailResult("postContent参数错误");
        }
        if (postTitle.trim().length() > 32) {
            return ResultGenerator.genFailResult("标题过长");
        }
        if (postContent.trim().length() > 100000) {
            return ResultGenerator.genFailResult("内容过长");
        }
        String kaptchaCode = httpSession.getAttribute(Constants.VERIFY_CODE_KEY) + "";
        if (!StringUtils.hasLength(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        post.setPostTitle(postTitle);
        post.setPostContent(postContent);
        post.setPostCategoryId(postCategoryId);
        post.setPostCategoryName(bbsPostCategory.getCategoryName());
        post.setLastUpdateTime(new Date());
        if (postService.updatePost(post)) {
            httpSession.removeAttribute(Constants.VERIFY_CODE_KEY);//清空session中的验证码信息
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("请求失败，请检查参数及账号是否有操作权限");
        }
    }
}
