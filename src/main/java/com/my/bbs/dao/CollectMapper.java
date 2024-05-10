package com.my.bbs.dao;

import com.my.bbs.entity.CollectEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectMapper {
    int deleteByPrimaryKey(Long recordId);

    int insert(CollectEntity record);

    int insertSelective(CollectEntity record);

    CollectEntity selectByPrimaryKey(Long recordId);

    CollectEntity selectByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    List<CollectEntity> listByUserId(@Param("userId") Long userId);

    int updateByPrimaryKeySelective(CollectEntity record);

    int updateByPrimaryKey(CollectEntity record);
}