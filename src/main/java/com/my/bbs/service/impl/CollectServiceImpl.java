package com.my.bbs.service.impl;

import com.my.bbs.dao.CollectMapper;
import com.my.bbs.dao.PostMapper;
import com.my.bbs.dao.UserMapper;
import com.my.bbs.entity.PostEntity;
import com.my.bbs.entity.CollectEntity;
import com.my.bbs.entity.UserEntity;
import com.my.bbs.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectServiceImpl implements CollectService {

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private PostMapper bbsPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Boolean addCollectRecord(Long userId, Long postId) {
        CollectEntity collectEntity = collectMapper.selectByUserIdAndPostId(userId, postId);
        UserEntity user = userMapper.selectByPrimaryKey(userId);
        // 验证账号是否存在或异常
        if (user == null || user.getUserStatus().intValue() == 1) {
            return false;
        }
        // 已经收藏了，则直接返回
        if (collectEntity != null) {
            return true;
        } else {
            // 未收藏
            collectEntity = new CollectEntity();
            collectEntity.setPostId(postId);
            collectEntity.setUserId(userId);
            PostEntity post = bbsPostMapper.selectByPrimaryKey(postId);
            post.setPostCollects(post.getPostCollects() + 1);     // 收藏数量加1
            // 新建收藏记录并修改帖子相关数据
            if (collectMapper.insertSelective(collectEntity) > 0 && bbsPostMapper.updateByPrimaryKey(post) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean deleteCollectRecord(Long userId, Long postId) {
        CollectEntity collectEntity = collectMapper.selectByUserIdAndPostId(userId, postId);
        UserEntity user = userMapper.selectByPrimaryKey(userId);
        // 验证账号是否存在或异常
        if (user == null || user.getUserStatus().intValue() == 1) {
            return false;
        }
        // 未收藏，直接返回
        if (collectEntity == null) {
            return true;
        } else {
            // 已收藏
            PostEntity post = bbsPostMapper.selectByPrimaryKey(postId);
            Long collectCount = post.getPostCollects() - 1;      // 收藏数量减1
            if (collectCount >= 0) {
                post.setPostCollects(collectCount);
            }
            // 删除收藏记录并修改帖子相关数据
            if (collectMapper.deleteByPrimaryKey(collectEntity.getRecordId()) > 0 && bbsPostMapper.updateByPrimaryKey(post) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean validUserCollect(Long userId, Long postId) {
        CollectEntity collectEntity = collectMapper.selectByUserIdAndPostId(userId, postId);
        if (collectEntity == null) {
            return false;
        }
        return true;
    }

    @Override
    public List<PostEntity> getCollectRecordsByUserId(Long userId) {
        List<CollectEntity> collectEntities = collectMapper.listByUserId(userId);
        if (!CollectionUtils.isEmpty(collectEntities)) {
            // 提取收藏记录中的postId
            List<Long> postIds = collectEntities.stream().map(CollectEntity::getPostId).collect(Collectors.toList());
            // 根据postIds查询相关post
            return bbsPostMapper.selectByPrimaryKeys(postIds);
        }
        return null;
    }
}
