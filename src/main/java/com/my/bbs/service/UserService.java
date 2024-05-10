package com.my.bbs.service;

import com.my.bbs.entity.UserEntity;
import jakarta.servlet.http.HttpSession;

public interface UserService {

    /* 用户注册 */
    String register(String loginName, String password, String nickName);

    /* 用户登录 */
    String login(String loginName, String passwordMD5, HttpSession httpSession);

    /* 获取用户详情 */
    UserEntity getUserById(Long userId);

    /* 修改用户信息 */
    Boolean updateUserInfo(UserEntity bbsUser, HttpSession httpSession);

    /* 修改用户头像 */
    Boolean updateUserHeadImg(UserEntity bbsUser, HttpSession httpSession);

    /* 修改密码 */
    Boolean updatePassword(Long userId, String originalPassword, String newPassword);
}