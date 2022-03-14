package ru.gb.springbootmarket.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {

    void store(MultipartFile file);

    void delete(Path location);
}
