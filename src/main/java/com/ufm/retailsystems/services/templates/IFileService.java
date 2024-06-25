package com.ufm.retailsystems.services.templates;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {
    void uploadFile(MultipartFile file) throws IOException;
}
