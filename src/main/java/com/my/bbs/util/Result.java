package com.my.bbs.util;

import java.io.Serializable;

/* 实现Serializable接口，可被序列化 */
public class Result<T> implements Serializable {
    /* 定义了序列化版本UID，确保序列化和反序列化的兼容性 */
    private static final long serialVersionUID = 1L;
    private int resultCode;         // 结果代码
    private String message;     // 结果消息
    private T data;             // 结果数据

    public Result() {
    }

    public Result(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
