package com.my.bbs.dao;

import com.my.bbs.entity.CategoryEntity;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer categoryId);

    int insert(CategoryEntity record);

    int insertSelective(CategoryEntity record);

    CategoryEntity selectByPrimaryKey(Integer categoryId);

    int updateByPrimaryKeySelective(CategoryEntity record);

    int updateByPrimaryKey(CategoryEntity record);

    List<CategoryEntity> getBBSPostCategories();
}