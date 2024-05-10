package com.my.bbs.util;

import java.security.MessageDigest;


public class MD5Util {

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /* 将二进制串转化成十六进制串 */
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));
        return resultSb.toString();
    }

    /* 单个二进制位的转换 */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");        // 获取MD5加密实例

            if (charsetname == null || "".equals(charsetname))
                // 未提供字符集，则使用默认，加密后的密文转化为16进制
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                // 提供了字符集，则使用，加密后的密文转化为16进制
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }


}
