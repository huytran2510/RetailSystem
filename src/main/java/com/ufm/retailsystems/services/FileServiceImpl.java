package com.ufm.retailsystems.services;

import com.ufm.retailsystems.services.templates.IFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements IFileService {
    private final String uploadDirectory = "static/img/";

    public void uploadFile(MultipartFile file) throws IOException {
        Path filePath = Paths.get(uploadDirectory + file.getOriginalFilename());
        Files.write(filePath, file.getBytes());
    }
}
