package com.ufm.retailsystems.services;

import com.ufm.retailsystems.services.templates.IFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements IFileService {
    @Value("${app.upload.dir}") // Cấu hình đường dẫn lưu trữ file trong file application.properties
    private String uploadDirectory;

    public void uploadFile(MultipartFile file) throws IOException {
        // Tạo đường dẫn đầy đủ cho file được tải lên
        Path filePath = Paths.get(uploadDirectory + "/" + file.getOriginalFilename());
        Files.write(filePath, file.getBytes());
    }
}
