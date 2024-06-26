package com.my.bbs.config;

import com.my.bbs.common.Constants;
import com.my.bbs.interceptor.MyBBSLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/* 设置登录拦截器和上传文件的静态资源处理器 */
@Configuration
public class MyBBSWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private MyBBSLoginInterceptor myBBSLoginInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        // 登陆拦截
        registry.addInterceptor(myBBSLoginInterceptor)
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/find")
                .excludePathPatterns("/reset")
                .addPathPatterns("/logout")
                .addPathPatterns("/addPostPage")
                .addPathPatterns("/addPost")
                .addPathPatterns("/addCollect/**")
                .addPathPatterns("/editPostPage/**")
                .addPathPatterns("/editPost")
                .addPathPatterns("/detail/**")
                .addPathPatterns("/uploadFile")
                .addPathPatterns("/uploadFiles")
                .addPathPatterns("/updateUserInfo")
                .addPathPatterns("/updateHeadImg")
                .addPathPatterns("/updatePassword")
                .addPathPatterns("/userCenter")
                .addPathPatterns("/userCenter/**")
                .addPathPatterns("/myCenter")
                .addPathPatterns("/userSet");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /* 以/upload/开头的请求会被映射到Constants.FILE_UPLOAD_DIC */
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
    }
}
