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
    @Value("${upload.shop.defaultImg}")
    public String defaultShopImg;

    @Value("${upload.stock.defaultImg}")
    public String defaultStockImg;

    public final String FORBIDDEN_PAGE = "forbiddenpage";
    public final String SEARCH_VALUE = "searchValue";
    public final String SEARCH_BY_ID = "searchById";
    public final String SEARCH_BY_TITLE = "searchByTitle";
    public final String SEARCH_BY_ADDRESS = "searchByAddress";
    public final String SEARCH_ACTIVE_CHECKBOX = "on";
    public  final long LIMIT_ENTITY_REQUEST = 10;

    /*
    return List. 0 el = typeSearch, 1 el = value
     */
    public List<String> getSearchValues(MultiValueMap<String, String> map) {
        List<String> arr = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            if (entry.getValue().get(0).equals(SEARCH_ACTIVE_CHECKBOX)) {

                String type = Strings.EMPTY;
                String value = map.get(SEARCH_VALUE).get(0);

                switch (entry.getKey()) {
                    case SEARCH_BY_ID:
                        type = SEARCH_BY_ID;
                        break;
                    case SEARCH_BY_TITLE:
                        type = SEARCH_BY_TITLE;
                        break;

                    case SEARCH_BY_ADDRESS:
                        type = SEARCH_BY_ADDRESS;
                        break;
                }

                arr.add(type);
                arr.add(value);
                break;
            }
        }

        return arr;
    }
}
