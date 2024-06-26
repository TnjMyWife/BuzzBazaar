package com.my.bbs.controller.common;


import com.my.bbs.common.Constants;
import com.my.bbs.entity.UserEntity;
import com.my.bbs.entity.ValidateEntity;
import com.my.bbs.service.UserService;
import com.my.bbs.service.ValidateService;
import com.my.bbs.util.Result;
import com.my.bbs.util.ResultGenerator;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class ValidateController {
    @Resource
    private ValidateService validateService;
    @Resource
    private UserService userService;
    //发送邮件的类
    @Resource
    private JavaMailSender mailSender;

    //这里使用的是我们已经在配置问价中固定了的变量值,也就是通过这个邮箱向目标邮箱发送重置密码的邮件
    @Value("${spring.mail.username}")
    private String from;

    /* 发送忘记密码邮件请求，每日申请次数不超过20次，每次申请间隔不低于1分钟 */
    @PostMapping("/sendValidationEmail")
    @ResponseBody
    public Result sendValidationEmail(@RequestParam("loginName") String email) throws MessagingException {
        UserEntity user = userService.getUserByLoginName(email);
        System.out.println(user);
        if (user == null){
            return ResultGenerator.genFailResult("邮箱所属用户不存在");
        }else {
            if (validateService.sendValidateLimitation(email, 20,1)){
                // 若允许重置密码，则在pm_validate表中插入一行数据，带有token
                ValidateEntity validate = new ValidateEntity();
                validateService.insertNewResetRecord(validate, user, UUID.randomUUID().toString());
                // 设置邮件内容
                //String appUrl = "http://localhost";
                String appUrl = "http://8.134.124.93";
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                // multipart模式
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
                mimeMessageHelper.setTo(email);
                mimeMessageHelper.setFrom(from);
                mimeMessageHelper.setSubject("重置密码");
                // 构建HTML内容
                // 构建HTML内容
                StringBuilder sb = new StringBuilder();
                sb.append("<html><head>")
                        .append("<style>")
                        .append("body {font-family: Arial, sans-serif; text-align: center;}")
                        .append("h1 {color: #333;}")
                        .append("p {margin: 20px 0;}")
                        .append("a {background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; display: inline-block; margin-top: 20px;}")
                        .append("</style>")
                        .append("</head>")
                        .append("<body>")
                        .append("<h1>点击下面的链接重置密码</h1>")
                        .append("<a href='").append(appUrl).append("/reset?token=").append(validate.getResetToken()).append("'>重置密码</a>")
                        .append("<p>如果您没有请求此操作，请忽略此邮件。</p>")
                        .append("<p>如果您有任何问题，请联系我们的客服支持。</p>")
                        .append("</body></html>");
                // 启用html
                mimeMessageHelper.setText(sb.toString(), true);
                validateService.sendPasswordResetEmail(mimeMessage);

                return ResultGenerator.genSuccessResult();
            }else {
                return ResultGenerator.genFailResult("操作过于频繁，请稍后再试！");
            }
        }
    }

    /* 将url的token和数据库里的token匹配，成功后便可修改密码，token有效期为5分钟 */
    @PostMapping("/resetPassword")
    @ResponseBody
    public Result resetPassword(HttpServletRequest request,
                                @RequestParam("token") String token,
                                @RequestParam("password") String password){
        System.out.println("进入了reset控制器");
        // 通过token找到validate记录
        ValidateEntity validate= validateService.findUserByResetToken(token);
        if (validate == null){
            return ResultGenerator.genFailResult("该重置请求不存在");
        }else {
            if (validateService.validateLimitation(validate.getEmail(), Long.MAX_VALUE, 5, token)){
                if (userService.resetPassword(validate.getUserId(), password)) {
                    // 修改成功后清空session中的数据, 需要重新登录
                    request.getSession().removeAttribute(Constants.USER_SESSION_KEY);
                    return ResultGenerator.genSuccessResult();
                } else {
                    return ResultGenerator.genFailResult("修改失败,请联系管理员修改");
                }
            }else {
                return ResultGenerator.genFailResult("该链接失效");
            }
        }
    }
}

