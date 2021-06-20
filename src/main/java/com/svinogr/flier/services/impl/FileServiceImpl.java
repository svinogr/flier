package com.svinogr.flier.services.impl;

import com.svinogr.flier.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.function.Function;

@Service
public class FileServiceImpl implements FileService {
    @Value("${upload.shop.imgPath}")
    private String upload;

    @Value("${upload.shop.defaultImg}")
    private String defaultImgShop;

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
        File uploadDir = new File(upload);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        String[] split = file.filename().split("\\.");
        String extension = split[split.length -1]; // получаем расширение
        String name = String.format("%d.%s", id, extension);
        String fullPath = String.format("%s%s", upload, name);

        file.transferTo(new File(fullPath));

        return Mono.just(fullPath).flatMap(fp -> {
            file.transferTo(new File(fp)).subscribe();
            return Mono.just(name);
        });
    }

    @Override
    public Mono<String> deleteImageForShop(String name) {
        return Mono.just(new File(upload  + name)).
                flatMap(file -> {
                    if(!name.equals(defaultImgShop)) {
                        file.delete();
                    }

                    return Mono.just(defaultImgShop);
                });
    }
}