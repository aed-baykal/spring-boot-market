package ru.gb.springbootmarket.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileSystemStorageService implements StorageService {

    @Value("${location}")
    private String location;
    private Path rootLocation;

    @PostConstruct
    void init() {
        rootLocation = Paths.get(location);
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Файл пустой");
            }
            Path destinationFile = rootLocation.resolve(Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Не возможно сохранить файл вне определенной директории");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения файла");
        }
    }

    @Override
    public void delete(Path location) {
        try {
            Path destinationFile = rootLocation.resolve(Paths.get(String.valueOf(location.getFileName())))
                    .normalize().toAbsolutePath();
            if (Files.exists(destinationFile)) Files.delete(destinationFile);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка удаления файла");
        }
    }
}
