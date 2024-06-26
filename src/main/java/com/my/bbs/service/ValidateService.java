package com.my.bbs.service;

import com.my.bbs.dao.ValidateMapper;
import com.my.bbs.entity.UserEntity;
import com.my.bbs.entity.ValidateEntity;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ValidateService{

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ValidateMapper validateMapper;

    /**
     * 发送邮件：@Async进行异步调用发送邮件接口
     * @param email
     */
//    @Async
//    public void sendPasswordResetEmail(SimpleMailMessage email){
//
//        javaMailSender.send(email);
//    }

    @Async
    public void sendPasswordResetEmail(MimeMessage email){
        javaMailSender.send(email);
    }

    /* 在pm_validate表中插入一条validate记录，userid，email属性来自pm_user表，token由UUID生成 */
    public int insertNewResetRecord(ValidateEntity validateEntity, UserEntity user, String token){
        validateEntity.setUserId(user.getUserId());
        validateEntity.setEmail(user.getLoginName());
        validateEntity.setResetToken(token);
        validateEntity.setType("passwordReset");
        validateEntity.setGmtCreate(new Date());
        validateEntity.setGmtModified(new Date());
        return validateMapper.insert(validateEntity);
    }

    /* pm_validate表中，通过token查找重置申请记录 */
    public ValidateEntity findUserByResetToken(String token){
        return validateMapper.selectByToken(token);
    }

    /* 验证是否发送重置邮件：每个email的重置密码每日请求上限为requestPerDay次，与上一次的请求时间间隔为interval分钟。 */
    public boolean sendValidateLimitation(String email, long requestPerDay, long interval){
        List<ValidateEntity> validateDaoList = validateMapper.selectByEmail(email);
        // 若查无记录，意味着第一次申请，直接放行
        if (validateDaoList.isEmpty()) {
            return true;
        }
        // 有记录，则判定是否频繁申请以及是否达到日均请求上线
        long countTodayValidation = validateDaoList.stream().filter(x-> DateUtils.isSameDay(x.getGmtModified(), new Date())).count();
        Optional validate = validateDaoList.stream().map(ValidateEntity::getGmtModified).max(Date::compareTo);
        Date dateOfLastRequest = new Date();
        if (validate.isPresent()) dateOfLastRequest = (Date) validate.get();
        long intervalForLastRequest = new Date().getTime() - dateOfLastRequest.getTime();

        return countTodayValidation <= requestPerDay && intervalForLastRequest >= interval * 60 * 1000;
    }

    /* 验证连接是否失效：链接有两种情况失效 1.超时 2.最近请求的一次链接自动覆盖之前的链接  */
    public boolean validateLimitation(String email, long requestPerDay, long interval, String token){
        List<ValidateEntity> validateDaoList = validateMapper.selectByEmail(email);
        // 有记录才会调用该函数，只需判断是否超时
        Optional validate = validateDaoList.stream().map(ValidateEntity::getGmtModified).max(Date::compareTo);
        Date dateOfLastRequest = new Date();
        if (validate.isPresent()) dateOfLastRequest = (Date) validate.get();
        long intervalForLastRequest = new Date().getTime() - dateOfLastRequest.getTime();

        Optional lastRequestToken = validateDaoList.stream().filter(x-> x.getResetToken().equals(token)).map(ValidateEntity::getGmtModified).findAny();
        Date dateOfLastRequestToken = new Date();
        if (lastRequestToken.isPresent()) {
            dateOfLastRequestToken = (Date) lastRequestToken.get();
        }
        return intervalForLastRequest <= interval * 60 * 1000 && dateOfLastRequest == dateOfLastRequestToken;
    }
}


