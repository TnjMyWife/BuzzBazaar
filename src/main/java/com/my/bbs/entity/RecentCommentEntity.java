package com.my.bbs.entity;

import java.util.Date;

/* 最近评论列表-实体类，个人中心页面展示时需要，只需要帖子ID、标题、评论内容、评论时间 */
public class RecentCommentEntity {

    private Long postId;

    private String postTitle;

    private String commentBody;

    private Date commentCreateTime;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public Date getCommentCreateTime() {
        return commentCreateTime;
    }

    public void setCommentCreateTime(Date commentCreateTime) {
        this.commentCreateTime = commentCreateTime;
    }
}