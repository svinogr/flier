package com.svinogr.flier.services;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.File;

/**
 * @author SVINOGR
 * version 0.0.1
 * <p>
 *Interface for file services
 */
public interface FileService {
    /**
     * Method getting img by name and path
     *
     * @param name name of img
     * @param path dir of img
     * @return file. Mono<File>
     */
    Mono<File> getImgByNameAndPath(String name, String path);

    /**
     * Method saving img for shop
     *
     * @param file file image {@link FilePart}
     * @param id shop id {@link com.svinogr.flier.model.shop.Shop}
     * @return name saved img
     */
    Mono<String> saveImgByIdForShop(FilePart file, Long id);

    /**
     * Method deleting img for shop
     *
     * @param name name of img
     * @return default name after deleting
     */
    Mono<String> deleteImageForShop(String name);

    /**
     * Method deleting img for stock
     *
     * @param name name of img
     * @return default name after deleting
     */
    Mono<String> deleteImageForStock(String name);

    /**
     * @param file Method saving img for stock
     * @param id stock id {@link com.svinogr.flier.model.shop.Stock}
     * @return name saved img
     */
    Mono<String> saveImgByIdForStock(FilePart file, Long id);
}
