package com.my.bbs.dao;

import com.my.bbs.entity.PostEntity;
import com.my.bbs.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostMapper {
    int deleteByPrimaryKey(Long postId);

    int insert(PostEntity record);

    int insertSelective(PostEntity record);

    PostEntity selectByPrimaryKey(Long postId);

    List<PostEntity> selectByPrimaryKeys(@Param("postIds")List<Long> postIds);

    int updateByPrimaryKeySelective(PostEntity record);

    int updateByPrimaryKeyWithBLOBs(PostEntity record);

    int updateByPrimaryKey(PostEntity record);

    int getTotalBBSPosts(PageQueryUtil pageUtil);

    List<PostEntity> findBBSPostList(PageQueryUtil pageUtil);

    List<PostEntity> getHotTopicBBSPostList();

    List<PostEntity> getMyBBSPostList(@Param("userId") Long userId, @Param("period") String period);
}