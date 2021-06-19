package com.svinogr.flier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class TemplateResolver {

/*    @Bean
    public ClassLoaderTemplateResolver adminTemplateResolver() {
        ClassLoaderTemplateResolver adminTemplateResolver = new ClassLoaderTemplateResolver();
        adminTemplateResolver.setPrefix("/templates/admin/");
        adminTemplateResolver.setSuffix(".html");
        adminTemplateResolver.setTemplateMode(TemplateMode.HTML);
        adminTemplateResolver.setCharacterEncoding("UTF-8");
        adminTemplateResolver.setOrder(0);
        adminTemplateResolver.setCheckExistence(true);

        return adminTemplateResolver;
    }


    @Bean
    public ClassLoaderTemplateResolver shopTemplateResolver() {
        ClassLoaderTemplateResolver shopTemplateResolver = new ClassLoaderTemplateResolver();
        shopTemplateResolver.setPrefix("/templates/shop/");
        shopTemplateResolver.setSuffix(".html");
        shopTemplateResolver.setTemplateMode(TemplateMode.HTML);
        shopTemplateResolver.setCharacterEncoding("UTF-8");
        shopTemplateResolver.setOrder(2);
        shopTemplateResolver.setCheckExistence(true);

        return shopTemplateResolver;
    }*/
}
