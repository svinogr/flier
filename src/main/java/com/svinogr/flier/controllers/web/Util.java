package com.svinogr.flier.controllers.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Util {
    @Value("${upload.shop.defaultImg}")
    public String defaultShopImg;

    @Value("${upload.stock.defaultImg}")
    public String defaultStockImg;

    public final String FORBIDDEN_PAGE = "forbiddenPage";
    public final String SEARCH_VALUE = "searchValue";
    public final String SEARCH_BY_ID = "searchById";
    public final String SEARCH_BY_TITLE = "searchByTitle";
    public final String SEARCH_BY_ADDRESS = "searchByAddress";
}
