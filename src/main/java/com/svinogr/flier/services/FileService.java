package com.svinogr.flier.services;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;


public interface FileService {
    Mono<File> getImgByNameAndPath(String name, String path);

    Mono<String> saveImgByIdForShop(FilePart file, Long id);

    Mono<String> deleteImageForShop(String name);
}
