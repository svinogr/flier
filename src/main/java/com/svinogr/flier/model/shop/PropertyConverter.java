package com.svinogr.flier.model.shop;

import com.svinogr.flier.model.PropertyShop;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class for converting string to PropertyShop from front thymeleaf"
 */
//TODO найти решение через динамическое получение данных из таблицы. а так это ебаный костылб!!!!
@Component
public class PropertyConverter implements Converter<String, PropertyShop> {

    private final List<PropertyShop> list = Arrays.asList(      new PropertyShop("FOOD", 1L), new PropertyShop(  "CLOTHES", 2L),
            new PropertyShop("BUILDING", 3L) );


    @Override
    public PropertyShop convert(String id) {
        long idBD = Long.parseLong(id);

        PropertyShop p = null;

        for (PropertyShop propertyShop: list) {
            if (propertyShop.getId() == idBD) p = propertyShop;

        }

        return p;
    }


}