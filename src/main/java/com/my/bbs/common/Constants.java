package com.my.bbs.common;

/* 常量配置 */
public class Constants {
    // 上传文件的默认url前缀，linux："/opt/image/upload/"左斜，windows双左斜"E://privat//Work//software enginering//project//upload/"

    /* 本机调试路径 */
    public final static String FILE_UPLOAD_DIC = "E://privat//Work//software enginering//project//upload/";

    /* 服务器路径："C://Users//Administrator//upload/" */
    // public final static String FILE_UPLOAD_DIC = "C://Users//Administrator//upload/";

    public final static String USER_SESSION_KEY = "myBBSUser"; //session中user的key

    public final static String VERIFY_CODE_KEY = "userVerifyCode";//验证码key
}
