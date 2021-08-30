package com.svinogr.flier.controllers.web.utils;

import lombok.Data;

@Data
public class PaginationUtil {
    public static final long ITEM_ON_PAGE= 10;
    private long currentPage;
    private long totalItem;
    private long pages;

    public PaginationUtil(long currentPage, long totalItem) {
        this.currentPage = currentPage;
        this.totalItem = totalItem;
        this.pages = (long) Math.ceil((double) totalItem/ITEM_ON_PAGE);
    }
}