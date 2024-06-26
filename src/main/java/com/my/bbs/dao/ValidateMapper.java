package com.my.bbs.dao;

import com.my.bbs.entity.ValidateEntity;
import java.util.List;

public interface ValidateMapper {
    int insert(ValidateEntity validate);

    ValidateEntity selectByToken(String token);

    List<ValidateEntity> selectByEmail(String email);
}
