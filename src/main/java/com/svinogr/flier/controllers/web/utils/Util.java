package com.svinogr.flier.controllers.web.utils;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Util {
    //TODO удалить и переделать все красиво
    @Value("${upload.shop.defaultImg}")
    public String defaultShopImg;

    @Value("${upload.stock.defaultImg}")
    public String defaultStockImg;

    public final String FORBIDDEN_PAGE = "forbiddenpage";
    public final String SEARCH_VALUE = "searchValue";
    public final String SEARCH_ACTIVE_CHECKBOX = "on";
    public  final long LIMIT_ENTITY_REQUEST = 10;
}
