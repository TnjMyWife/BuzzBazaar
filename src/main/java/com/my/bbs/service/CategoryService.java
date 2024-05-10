package com.my.bbs.service;

import com.my.bbs.entity.CategoryEntity;

import java.util.List;

public interface CategoryService {
    /* 获取分类列表 */
    List<CategoryEntity> getBBSPostCategories();
    /* 通过分类ID获取分类信息 */
    CategoryEntity selectById(Integer categoryId);
}
