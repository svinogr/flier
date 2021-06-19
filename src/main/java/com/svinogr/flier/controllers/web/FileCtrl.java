package com.svinogr.flier.controllers.web;

import com.svinogr.flier.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/img")
public class FileCtrl {
    @Autowired
    private FileService fileService;

    @Value("${upload.shop.imgPath}")
    private String shopImgPath;

    @GetMapping("shop/{id}")
    public Mono<Void> imgShopRout(@PathVariable String id, ServerHttpResponse response) throws IOException {
       return fileService.getImgByNameAndPath(id, shopImgPath).flatMap(file ->
        {
            ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
            response.getHeaders().setContentType(MediaType.valueOf("image/" + id.split("\\.")[1].toLowerCase()));
//            System.out.println(MediaType.valueOf("image/" + id.split("\\.")[1].toLowerCase()));
            return zeroCopyResponse.writeWith(file, 0, file.length());
        });
    }
}
