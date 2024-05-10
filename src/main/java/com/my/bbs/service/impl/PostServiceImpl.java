package com.my.bbs.service.impl;

import com.my.bbs.dao.CategoryMapper;
import com.my.bbs.dao.PostMapper;
import com.my.bbs.dao.UserMapper;
import com.my.bbs.entity.*;
import com.my.bbs.service.PostService;
import com.my.bbs.util.BeanUtil;
import com.my.bbs.util.PageQueryUtil;
import com.my.bbs.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean savePost(PostEntity post) {
        UserEntity user = userMapper.selectByPrimaryKey(post.getPublishUserId());
        // 检查账号异常
        if (user == null || user.getUserStatus().intValue() == 1) {
            return false;
        }
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(post.getPostCategoryId());
        // 检查分类数据
        if (categoryEntity == null) {
            return false;
        }
        return postMapper.insertSelective(post) > 0;
    }

    @Override
    public PostEntity getPostById(Long postId) {
        return postMapper.selectByPrimaryKey(postId);
    }

    @Override
    public PostEntity getPostForDetail(Long postId) {
        PostEntity post = postMapper.selectByPrimaryKey(postId);
        if (post != null) {
            post.setPostViews(post.getPostViews() + 1);
            postMapper.updateByPrimaryKeySelective(post);
        }
        return post;
    }

    @Override
    public Boolean updatePost(PostEntity post) {
        UserEntity user = userMapper.selectByPrimaryKey(post.getPublishUserId());
        // 检查账号异常
        if (user == null || user.getUserStatus().intValue() == 1) {
            return false;
        }
        // 检查分类数据
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(post.getPostCategoryId());
        if (categoryEntity == null) {
            return false;
        }
        return postMapper.updateByPrimaryKeySelective(post) >0;
    }

    @Override
    public Boolean deletePost(Long userId, Long postId) {
        UserEntity user = userMapper.selectByPrimaryKey(userId);
        // 检查账号异常
        if (user == null || user.getUserStatus().intValue() == 1) {
            return false;
        }
        PostEntity post = postMapper.selectByPrimaryKey(postId);
        // 只允许发帖人删除
        if (post != null && post.getPublishUserId().equals(userId)) {
            return postMapper.deleteByPrimaryKey(postId) > 0;
        }
        return false;
    }

    @Override
    public PageResult getPagePosts(PageQueryUtil pageUtil) {
        // 查询当前页面帖子数据
        int total = postMapper.getTotalBBSPosts(pageUtil);
        List<PostEntity> postEntities = postMapper.findBBSPostList(pageUtil);
        List<PagePostEntity> pagePostEntities = new ArrayList<>();
        // 将查询的帖子实体转化为页面显示的帖子实体
        if (!CollectionUtils.isEmpty(postEntities)) {
            pagePostEntities = BeanUtil.copyList(postEntities, PagePostEntity.class);
            // 获取所有帖子的发帖者id
            List<Long> userIds = pagePostEntities.stream().map(PagePostEntity::getPublishUserId).collect(Collectors.toList());
            // 获取发帖人的数据
            List<UserEntity> userEntities = userMapper.selectByPrimaryKeys(userIds);
            if (!CollectionUtils.isEmpty(userEntities)) {
                // 转化为map方便使用
                Map<Long, UserEntity> userMap = userEntities.stream().collect(Collectors.toMap(UserEntity::getUserId, Function.identity(), (entity1, entity2) -> entity1));
                // 对每个用于页面显示的帖子，设置头像和昵称
                for (PagePostEntity pagePostEntity : pagePostEntities) {
                    if (userMap.containsKey(pagePostEntity.getPublishUserId())) {
                        UserEntity tempUser = userMap.get(pagePostEntity.getPublishUserId());
                        pagePostEntity.setHeadImgUrl(tempUser.getHeadImgUrl());
                        pagePostEntity.setNickName(tempUser.getNickName());
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(pagePostEntities, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public List getHotPosts() {
        List<PostEntity> postEntities = postMapper.getHotTopicBBSPostList();
        List<HotPostEntity> hotPostEntities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(postEntities)) {
            hotPostEntities = BeanUtil.copyList(postEntities, HotPostEntity.class);
        }
        return hotPostEntities;
    }

    @Override
    public List<PostEntity> getMyPosts(Long userId) {
        return postMapper.getMyBBSPostList(userId, "all");
    }

    @Override
    public List<PostEntity> getRecentPostListByUserId(Long userId) { return postMapper.getMyBBSPostList(userId, "recent"); }
}
