package com.svinogr.flier.controllers.web;

import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.stream.Collectors;

/**@author SVINOGR
 * @version 0.0.1
 *
 * Class for managing error for formdata page
 */
public class CtrlUtils {
    /**
     * @param bindingResult {@link BindingResult}
     * @return map {@link Map} with result with error of data form
     */
    public static Map<String, String> getErrors(BindingResult bindingResult) {
        Map<String, String> collect = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(
                fieldError -> fieldError.getField() +"Error",
                fieldError -> fieldError.getDefaultMessage()
        ));

        return collect;
    }
}
