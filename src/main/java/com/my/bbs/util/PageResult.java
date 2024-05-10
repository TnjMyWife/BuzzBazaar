package com.my.bbs.util;

import java.io.Serializable;
import java.util.List;

/* 当前页面结果，含页面所需数据列表、分页等信息 */
public class PageResult implements Serializable {
    private int totalCount;     // 总记录数
    private int pageSize;       // 每页记录数
    private int totalPage;      // 总页数
    private int currPage;       // 当前页数
    private List<?> list;        // 列表数据，存储当前页的数据列表

    public PageResult(List<?> list, int totalCount, int pageSize, int currPage) {
        this.list = list;           // 数据
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);       // 根据总记录数和每页记录数计算
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

}
