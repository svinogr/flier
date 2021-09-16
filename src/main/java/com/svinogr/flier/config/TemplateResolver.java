package com.svinogr.flier.config;

import com.svinogr.flier.config.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Class Resolver for static resourse.
 *
 */
@Configuration
public class TemplateResolver {

    @Bean
    public ClassLoaderTemplateResolver adminTemplateResolver() {
        ClassLoaderTemplateResolver adminTemplateResolver = new ClassLoaderTemplateResolver();
        adminTemplateResolver.setPrefix("/templates/account/admin/");
        adminTemplateResolver.setSuffix(".html");
        adminTemplateResolver.setTemplateMode(TemplateMode.HTML);
        adminTemplateResolver.setCharacterEncoding("UTF-8");
        adminTemplateResolver.setOrder(0);
        adminTemplateResolver.setCheckExistence(true);

        return adminTemplateResolver;
    }

    @Bean
    public ClassLoaderTemplateResolver adminsTemplateResolver() {
        ClassLoaderTemplateResolver adminTemplateResolver = new ClassLoaderTemplateResolver();
        adminTemplateResolver.setPrefix("/templates/account/admin/common/");
        adminTemplateResolver.setSuffix(".html");
        adminTemplateResolver.setTemplateMode(TemplateMode.HTML);
        adminTemplateResolver.setCharacterEncoding("UTF-8");
        adminTemplateResolver.setOrder(4);
        adminTemplateResolver.setCheckExistence(true);

        return adminTemplateResolver;
    }

    @Bean
    public ClassLoaderTemplateResolver shopTemplateResolver() {
        ClassLoaderTemplateResolver shopTemplateResolver = new ClassLoaderTemplateResolver();
        shopTemplateResolver.setPrefix("/templates/account/");
        shopTemplateResolver.setSuffix(".html");
        shopTemplateResolver.setTemplateMode(TemplateMode.HTML);
        shopTemplateResolver.setCharacterEncoding("UTF-8");
        shopTemplateResolver.setOrder(2);
        shopTemplateResolver.setCheckExistence(true);

        return shopTemplateResolver;
    }
}
