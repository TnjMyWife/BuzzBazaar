package com.my.bbs.util;

import org.springframework.util.StringUtils;

/* 响应结果生成工具 */
public class ResultGenerator {
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";
    private static final String DEFAULT_FAIL_MESSAGE = "FAIL";
    private static final int RESULT_CODE_SUCCESS = 200;
    private static final int RESULT_CODE_SERVER_ERROR = 500;

    /* 生成一个表示成功的结果对象，使用默认的成功消息和状态码 */
    public static Result genSuccessResult() {
        Result result = new Result();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        return result;
    }
    /* 生成成功结果，使用传入消息 */
    public static Result genSuccessResult(String message) {
        Result result = new Result();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(message);
        return result;
    }
    /* 生成成功的结果，使用传入数据 */
    public static Result genSuccessResult(Object data) {
        Result result = new Result();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        result.setData(data);
        return result;
    }

    /* 生成失败结果 */
    public static Result genFailResult(String message) {
        Result result = new Result();
        result.setResultCode(RESULT_CODE_SERVER_ERROR);
        if (!StringUtils.hasLength(message)) {
            result.setMessage(DEFAULT_FAIL_MESSAGE);
        } else {
            result.setMessage(message);
        }
        return result;
    }

    public static Result genErrorResult(int code, String message) {
        Result result = new Result();
        result.setResultCode(code);
        result.setMessage(message);
        return result;
    }
}
