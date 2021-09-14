package com.svinogr.flier.services.impl;

import com.svinogr.flier.services.FileService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Class service implementation {@link FileService}
 */
@Service
public class FileServiceImpl implements FileService {
    @Value("${upload.shop.imgPath}")
    private String uploadshopImg;

    @Value("${upload.stock.imgPath}")
    private String uploadStockImg;

    @Value("${upload.shop.defaultImg}")
    private String defaultImgShop;

    @Value("${upload.stock.defaultImg}")
    private String defaultImgStock;


    @Override
    public Mono<File> getImgByNameAndPath(String name, String path) {
        File file = new File(path  + name);

        if (!file.exists()){
            file = new File(path  + defaultImgShop);
        }

        return Mono.just(file);
    }

    @Override
    public Mono<String> saveImgByIdForShop(FilePart file, Long id) {
        return getNameImg(file, id, uploadshopImg);
    }

    @NotNull
    private Mono<String> getNameImg(FilePart file, Long id, String forWhoEntity) {
        File uploadDir = new File(uploadshopImg);

        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        String[] split = file.filename().split("\\.");
        String extension = split[split.length -1]; // получаем расширение
        String name = String.format("%d.%s", id, extension);
        String fullPath = String.format("%s%s", uploadshopImg, name);

        file.transferTo(new File(fullPath));

        return Mono.just(fullPath).flatMap(fp -> {
            file.transferTo(new File(fp)).subscribe();
            return Mono.just(name);
        });
    }


    @Override
    public Mono<String> saveImgByIdForStock(FilePart file, Long id) {
        return getNameImg(file, id, uploadStockImg);
    }

    @Override
    public Mono<String> deleteImageForShop(String name) {
        return Mono.just(new File(uploadshopImg + name)).
                flatMap(file -> {
                    if(!name.equals(defaultImgShop)) {
                        file.delete();
                    }

                    return Mono.just(defaultImgShop);
                });
    }

    @Override
    public Mono<String> deleteImageForStock(String name) {
        return Mono.just(new File(uploadshopImg + name)).
                flatMap(file -> {
                    if(!name.equals(defaultImgStock)) {
                        file.delete();
                    }

                    return Mono.just(defaultImgStock);
                });
    }
}
