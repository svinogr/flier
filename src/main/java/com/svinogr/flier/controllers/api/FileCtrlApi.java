package com.svinogr.flier.controllers.api;

import com.svinogr.flier.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/api")
public class FileCtrlApi {
    @Autowired
    private FileService fileService;

    @Value("${upload.shop.imgPath}")
    private String shopImgPath;

    @Value("${upload.stock.imgPath}")
    private String stockImgPath;

    /**
     * @param imgName name of image shop 'id.extension'
     * @param response {@link ServerHttpResponse}
     * @return data of pic for shop
     */
    @GetMapping("img/shop/{imgName}")
    public Mono<Void> imgShopRout(@PathVariable String imgName, ServerHttpResponse response)  {
        return fileService.getImgByNameAndPath(imgName, shopImgPath)
                .flatMap(file ->
                {
                    ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
                    response.getHeaders().setContentType(MediaType.valueOf("image/" + imgName.split("\\.")[1].toLowerCase()));
                    return zeroCopyResponse.writeWith(file, 0, file.length());
                });
    }

    /**
     * @param imgName name of image stock 'id.extension'
     * @param response {@link ServerHttpResponse}
     * @return data of pic for stock
     */
    @GetMapping("img/stock/{imgName}")
    public Mono<Void> imgStockRout(@PathVariable String imgName, ServerHttpResponse response)  {
        return fileService.getImgByNameAndPath(imgName, stockImgPath)
                .flatMap(file ->
                {
                    ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
                    response.getHeaders().setContentType(MediaType.valueOf("image/" + imgName.split("\\.")[1].toLowerCase()));
                    return zeroCopyResponse.writeWith(file, 0, file.length());
                });
    }
}
