package com.my.bbs.service;

import com.my.bbs.entity.PostEntity;
import com.my.bbs.util.PageQueryUtil;
import com.my.bbs.util.PageResult;

import java.util.List;

public interface PostService {

    /* 保存帖子 */
    Boolean savePost(PostEntity Post);

    /* 获取帖子详细信息 */
    PostEntity getPostById(Long postId);

    /* 浏览帖子 */
    PostEntity getPostForDetail(Long postId);

    /* 修改帖子 */
    Boolean updatePost(PostEntity post);

    /* 删除帖子 */
    Boolean deletePost(Long userId, Long postId);

    /* 获取首页帖子列表 */
    PageResult getPagePosts(PageQueryUtil pageUtil);

    /* 热门帖子列表 */
    List getHotPosts();

    /* 根据userId查询发布的所有帖子 */
    List<PostEntity> getMyPosts(Long userId);

    /* 根据userId获取最近发帖列表 */
    List<PostEntity> getRecentPostListByUserId(Long userId);
}
