package com.my.bbs.service;

import com.my.bbs.entity.PostEntity;

import java.util.List;

public interface CollectService {

    /* 当前用户收藏帖子 */
    Boolean addCollectRecord(Long userId, Long postId);

    /* 当前用户取消收藏帖子 */
    Boolean deleteCollectRecord(Long userId, Long postId);

    /* 验证当前用户是否收藏了帖子 */
    Boolean validUserCollect(Long userId, Long postId);

    /* 获取当前用户收藏的帖子列表 */
    List<PostEntity> getCollectRecordsByUserId(Long userId);
}
