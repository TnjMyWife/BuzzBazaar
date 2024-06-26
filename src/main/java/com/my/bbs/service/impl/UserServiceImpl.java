package com.my.bbs.service.impl;

import com.my.bbs.common.Constants;
import com.my.bbs.common.ServiceResultEnum;
import com.my.bbs.dao.UserMapper;
import com.my.bbs.entity.UserEntity;
import com.my.bbs.service.UserService;
import com.my.bbs.util.MD5Util;
import com.my.bbs.util.SystemUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public String register(String loginName, String password, String nickName, String area) {
        // 用户名已存在
        if (userMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        // 注册用户
        UserEntity newUser = new UserEntity();
        newUser.setLoginName(loginName);
        newUser.setNickName(nickName);
        // 使用默认头像
        newUser.setHeadImgUrl("/images/avatar/default.png");
        // 默认介绍
        newUser.setIntroduce("这个人很懒，什么都没留下~");
        // 居住地
        newUser.setLocation(area);
        // 性别
        newUser.setGender("未知");
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        newUser.setPasswordMd5(passwordMD5);
        if (userMapper.insertSelective(newUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String loginName, String passwordMD5, String area, HttpSession httpSession) {
        UserEntity user = userMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        // 检查用户对象和会话对象
        if (user != null && httpSession != null) {
            // 将当前用户保存到会话中
            httpSession.setAttribute(Constants.USER_SESSION_KEY, user);
            // 修改上一次登录时间
            user.setLastLoginTime(new Date());
            // 修改所在位置
            user.setLocation(area);
            userMapper.updateByPrimaryKeySelective(user);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public Boolean updateUserInfo(UserEntity user, HttpSession httpSession) {
        UserEntity currentUser = (UserEntity) httpSession.getAttribute(Constants.USER_SESSION_KEY);
        UserEntity userFromDB = userMapper.selectByPrimaryKey(currentUser.getUserId());
        // 只有正常用户可修改
        if (userFromDB != null && userFromDB.getUserStatus().intValue() == 0) {
            userFromDB.setIntroduce(SystemUtil.cleanString(user.getIntroduce()));
            userFromDB.setLocation(SystemUtil.cleanString(user.getLocation()));
            userFromDB.setGender(SystemUtil.cleanString(user.getGender()));
            userFromDB.setNickName(SystemUtil.cleanString(user.getNickName()));
            if (userMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
                httpSession.setAttribute(Constants.USER_SESSION_KEY, userFromDB);
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean updateUserHeadImg(UserEntity user, HttpSession httpSession) {
        UserEntity userFromDB = userMapper.selectByPrimaryKey(user.getUserId());
        // 只有正常用户可修改
        if (userFromDB != null && userFromDB.getUserStatus().intValue() == 0) {
            userFromDB.setHeadImgUrl(user.getHeadImgUrl());
            if (userMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
                httpSession.setAttribute(Constants.USER_SESSION_KEY, userFromDB);
                return true;
            }
        }
        return false;
    }

    @Override
    public UserEntity getUserById(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public UserEntity getUserByLoginName(String loginName) {
        return userMapper.selectByLoginName(loginName);
    }

    @Override
    public Boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        UserEntity user = userMapper.selectByPrimaryKey(userId);
        // 只有正常用户可修改
        if (user != null && user.getUserStatus().intValue() == 0) {
            String originalPasswordMd5 = MD5Util.MD5Encode(oldPassword, "UTF-8");
            String newPasswordMd5 = MD5Util.MD5Encode(newPassword, "UTF-8");

            // 检查原密码是否正确
            if (originalPasswordMd5.equals(user.getPasswordMd5())) {
                // 设置新密码
                user.setPasswordMd5(newPasswordMd5);
                if (userMapper.updateByPrimaryKeySelective(user) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean resetPassword(Long userId, String password) {
        UserEntity user = userMapper.selectByPrimaryKey(userId);
        // 只有正常用户可修改
        if (user != null && user.getUserStatus().intValue() == 0) {
            String newPasswordMd5 = MD5Util.MD5Encode(password, "UTF-8");
            // 设置新密码
            user.setPasswordMd5(newPasswordMd5);
            if (userMapper.updateByPrimaryKeySelective(user) > 0) {
                return true;
            }
        }
        return false;
    }
}