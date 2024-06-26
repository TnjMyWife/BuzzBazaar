package com.my.bbs.service.impl;

import com.my.bbs.dao.CommentMapper;
import com.my.bbs.dao.PostMapper;
import com.my.bbs.dao.UserMapper;
import com.my.bbs.entity.*;
import com.my.bbs.service.CommentService;
import com.my.bbs.util.BeanUtil;
import com.my.bbs.util.PageQueryUtil;
import com.my.bbs.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Boolean addPostComment(CommentEntity postComment) {
        PostEntity bbsPost = postMapper.selectByPrimaryKey(postComment.getPostId());
        // 帖子不存在
        if (bbsPost == null) {
            return false;
        }
        UserEntity bbsUser = userMapper.selectByPrimaryKey(postComment.getCommentUserId());
        // 检查账户异常
        if (bbsUser == null || bbsUser.getUserStatus().intValue() == 1) {
            return false;
        }
        bbsPost.setPostComments(bbsPost.getPostComments() + 1);     // 评论数+1
        if (commentMapper.insertSelective(postComment) > 0 && postMapper.updateByPrimaryKeySelective(bbsPost) > 0){
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean delPostComment(Long commentId, Long userId) {
        CommentEntity bbsPostComment = commentMapper.selectByPrimaryKey(commentId);
        // 评论不存在
        if (bbsPostComment == null) {
            return false;
        }

        UserEntity bbsUser = userMapper.selectByPrimaryKey(userId);
        // 用户异常
        if (bbsUser == null || bbsUser.getUserStatus().intValue() == 1) {
            return false;
        }

        PostEntity bbsPost = postMapper.selectByPrimaryKey(bbsPostComment.getPostId());
        // 帖子不存在
        if (bbsPost == null) {
            return false;
        }

        Long commentCount = bbsPost.getPostComments() - 1;      // 评论数-1
        if (commentCount >= 0) {
            bbsPost.setPostComments(commentCount);
        }
        // 仅评论者或发帖者可以删除评论
        if (userId.equals(bbsPostComment.getCommentUserId()) || userId.equals(bbsPost.getPublishUserId())) {
            if (commentMapper.deleteByPrimaryKey(commentId) > 0 && postMapper.updateByPrimaryKeySelective(bbsPost) > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public PageResult getCommentsByPostId(Long postId, int commentPage) {
        Map params = new HashMap();
        params.put("postId", postId);
        params.put("page", commentPage);    // 当前页码
        params.put("limit", 6);         // 每页评论条数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        // 查询评论数据
        int total = commentMapper.getTotalComments(pageUtil);
        List<CommentEntity> commentList = commentMapper.findCommentList(pageUtil);
        List<PageCommentEntity> pageCommentEntities = new ArrayList<>();
        // 将查询评论实体转化为页面显示的评论实体
        if (!CollectionUtils.isEmpty(commentList)) {
            pageCommentEntities = BeanUtil.copyList(commentList, PageCommentEntity.class);
            // 获取所有评论者id
            List<Long> userIds = pageCommentEntities.stream().map(PageCommentEntity::getCommentUserId).collect(Collectors.toList());
            // 获取所有被回复者的id
            List<Long> parentUserIds = pageCommentEntities.stream().map(PageCommentEntity::getParentCommentUserId).collect(Collectors.toList());
            // 分别查询用户数据
            List<UserEntity> users = userMapper.selectByPrimaryKeys(userIds);
            List<UserEntity> parentUsers = userMapper.selectByPrimaryKeys(parentUserIds);
            // 向所有评论实体添加评论头像和昵称
            if (!CollectionUtils.isEmpty(users)) {
                // 创建从评论者id到自身的map，方便使用
                Map<Long, UserEntity> userMap = users.stream().collect(Collectors.toMap(UserEntity::getUserId, Function.identity(), (entity1, entity2) -> entity1));
                // 对于每一个新的pageComment实体对象，加入评论者的头像和昵称
                for (PageCommentEntity pageCommentEntity : pageCommentEntities) {
                    if (userMap.containsKey(pageCommentEntity.getCommentUserId())) {
                        // 根据id访问map中的信息，并设置头像字段和昵称字段
                        UserEntity tempUser = userMap.get(pageCommentEntity.getCommentUserId());
                        pageCommentEntity.setHeadImgUrl(tempUser.getHeadImgUrl());
                        pageCommentEntity.setNickName(tempUser.getNickName());
                        pageCommentEntity.setLocation(tempUser.getLocation());
                    }
                }
            }
            // 向所有评论实体的评论内容添加被回复者的信息，如果它是回复的帖子。
            if (!CollectionUtils.isEmpty(parentUsers)) {
                // 创建从被回复者id到自身的map，方便使用
                Map<Long, UserEntity> parentUserMap = parentUsers.stream().collect(Collectors.toMap(UserEntity::getUserId, Function.identity(), (entity1, entity2) -> entity1));
                // 对于每一个新的pageComment实体对象，向评论内容添加被回复者信息
                for (PageCommentEntity bbsCommentListEntity : pageCommentEntities) {
                    if (parentUserMap.containsKey(bbsCommentListEntity.getParentCommentUserId())) {
                        // 在有父评论的评论内容前加上"@被回复者昵称 "
                        UserEntity tempUser = parentUserMap.get(bbsCommentListEntity.getParentCommentUserId());
                        bbsCommentListEntity.setCommentBody("@" + tempUser.getNickName() + "：" + bbsCommentListEntity.getCommentBody());
                    }
                }
            }


        }
        PageResult pageResult = new PageResult(pageCommentEntities, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public List<RecentCommentEntity> getRecentCommentListByUserId(Long userId) {
        // 获取当前用户所有评论
        List<CommentEntity> commentEntities = commentMapper.getRecentCommentListByUserId(userId);
        List<RecentCommentEntity> recentCommentEntities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(commentEntities)) {
            recentCommentEntities = BeanUtil.copyList(commentEntities, RecentCommentEntity.class);
            // 从评论中获取所有帖子的id
            List<Long> postIds = commentEntities.stream().map(CommentEntity::getPostId).collect(Collectors.toList());
            // 根据所有帖子id获取所有帖子信息
            List<PostEntity> postEntities = postMapper.selectByPrimaryKeys(postIds);
            // 根据帖子信息修改recentCommentEntities的信息
            if (!CollectionUtils.isEmpty(postEntities)) {
                Map<Long, PostEntity> postMap = postEntities.stream().collect(Collectors.toMap(PostEntity::getPostId, Function.identity(), (entity1, entity2) -> entity1));
                for (RecentCommentEntity recentCommentListEntity : recentCommentEntities) {
                    if (postMap.containsKey(recentCommentListEntity.getPostId())) {
                        // 设置帖子标题
                        PostEntity tempPost = postMap.get(recentCommentListEntity.getPostId());
                        recentCommentListEntity.setPostTitle(tempPost.getPostTitle());
                    }
                }
            }
        }
        return recentCommentEntities;
    }
}
