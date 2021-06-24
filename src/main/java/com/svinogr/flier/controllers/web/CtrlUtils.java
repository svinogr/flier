package com.svinogr.flier.controllers.web;

import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.stream.Collectors;

public class CtrlUtils {
    public static Map<String, String> getErrors(BindingResult bindingResult) {
        Map<String, String> collect = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(
                fieldError -> fieldError.getField() +"Error",
                fieldError -> fieldError.getDefaultMessage()
        ));
        System.out.println(collect.keySet());
        return collect;
    }
}
