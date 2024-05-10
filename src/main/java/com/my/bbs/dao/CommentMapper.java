package com.my.bbs.dao;

import com.my.bbs.entity.CommentEntity;
import com.my.bbs.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    int deleteByPrimaryKey(Long commentId);

    int insert(CommentEntity record);

    int insertSelective(CommentEntity record);

    CommentEntity selectByPrimaryKey(Long commentId);

    int updateByPrimaryKeySelective(CommentEntity record);

    int updateByPrimaryKey(CommentEntity record);

    int getTotalComments(PageQueryUtil pageUtil);

    List<CommentEntity> findCommentList(PageQueryUtil pageUtil);

    List<CommentEntity> getRecentCommentListByUserId(@Param("userId") Long userId);
}