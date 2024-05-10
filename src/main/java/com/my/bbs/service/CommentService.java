package com.my.bbs.service;

import com.my.bbs.entity.CommentEntity;
import com.my.bbs.entity.RecentCommentEntity;
import com.my.bbs.util.PageResult;

import java.util.List;

public interface CommentService {

    /* 用户进行评论 */
    Boolean addPostComment(CommentEntity postComment);

    /* 用户删除评论 */
    Boolean delPostComment(Long commentId, Long userId);

    /* 获取帖子评论列表 */
    PageResult getCommentsByPostId(Long postId, int commentPage);

    /* 获取最近评论列表 */
    List<RecentCommentEntity> getRecentCommentListByUserId(Long userId);
}
