package com.minimall.common;

import lombok.Data;

import java.util.List;

/**
 * 分页结果
 *
 * @param <T> 记录类型
 */
@Data
public class PageResult<T> {

    /** 总记录数 */
    private long total;

    /** 当前页记录 */
    private List<T> records;

    public PageResult() {
    }

    public PageResult(long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}
