package com.my.bbs.controller.common;

import com.my.bbs.common.Constants;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* 动态验证码控制器 */
@Controller
public class CaptchaController {

    @GetMapping("/common/captcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/gif");
        // 创建GIF验证码,指定长、宽、位
        GifCaptcha captcha = new GifCaptcha(75, 30,4);
        // 设置类型为数字和字母混合
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        // 设置字体
        captcha.setCharType(Captcha.FONT_9);
        // 验证码存入session
        httpServletRequest.getSession().setAttribute(Constants.VERIFY_CODE_KEY, captcha.text().toLowerCase());
        // 输出图片流
        captcha.out(httpServletResponse.getOutputStream());
    }
}
