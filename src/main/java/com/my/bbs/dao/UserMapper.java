package com.my.bbs.dao;

import com.my.bbs.entity.UserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(UserEntity record);

    int insertSelective(UserEntity record);

    UserEntity selectByPrimaryKey(Long userId);

    List<UserEntity> selectByPrimaryKeys(@Param("userIds") List<Long> userIds);

    UserEntity selectByLoginName(String loginName);

    UserEntity selectByLoginNameAndPasswd(@Param("loginName") String loginName, @Param("password") String password);

    int updateByPrimaryKeySelective(UserEntity record);

    int updateByPrimaryKey(UserEntity record);
}