package com.my.bbs.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* 匹配格式的工具类 */
public class PatternUtil {

    /* 匹配邮箱 */
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /* 判断是否是邮箱 */
    public static boolean isEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

}
