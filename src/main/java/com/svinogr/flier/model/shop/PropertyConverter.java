package com.svinogr.flier.model.shop;

import com.svinogr.flier.model.PropertyShop;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 * Class for converting string to PropertyShop from front thymeleaf"
 */
@Component
public class PropertyConverter implements Converter<String, PropertyShop> {
    @Override
    public PropertyShop convert(String id) {
        long idBD = Long.parseLong(id);

        PropertyShop propertyShop = new PropertyShop();
        propertyShop.setId(idBD);

        return propertyShop;
    }
}
