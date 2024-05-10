package com.my.bbs.service.impl;

import com.my.bbs.dao.CategoryMapper;
import com.my.bbs.entity.CategoryEntity;
import com.my.bbs.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public List<CategoryEntity> getBBSPostCategories() {
        return categoryMapper.getBBSPostCategories();
    }
    @Override
    public CategoryEntity selectById(Integer categoryId) {
        return categoryMapper.selectByPrimaryKey(categoryId);
    }
}
