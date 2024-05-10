package com.my.bbs.util;

import java.util.LinkedHashMap;
import java.util.Map;

/* 分页查询参数，继承java.util.LinkedHashMap */
public class PageQueryUtil extends LinkedHashMap<String, Object> {
    private int page;       //当前页码
    private int limit;      //每页条数

    public PageQueryUtil(Map<String, Object> params) {
        this.putAll(params);        // 将传入的所有键值对添加到当前对象中。
        //分页参数
        this.page = Integer.parseInt(params.get("page").toString());
        this.limit = Integer.parseInt(params.get("limit").toString());
        this.put("start", (page - 1) * limit);      // 起始页码
        this.put("page", page);                     // 当前页码
        this.put("limit", limit);                   // 每页条数
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "PageUtil{" +
                "page=" + page +
                ", limit=" + limit +
                '}';
    }
}
