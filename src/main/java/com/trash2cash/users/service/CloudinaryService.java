package com.trash2cash.users.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.trash2cash.config.CloudinaryConfiguration;
import com.trash2cash.users.utils.UploadResponse;
import org.cloudinary.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
@Service
public class CloudinaryService {
    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);

    private final Cloudinary cloudinaryClient;

    @Autowired
    public CloudinaryService(final CloudinaryConfiguration configuration) {
        this.cloudinaryClient =
                new Cloudinary(
                        String.format(
                                "cloudinary://%s:%s@%s",
                                configuration.getApiKey(),
                                configuration.getApiSecret(),
                                configuration.getCloudName()));
    }

    public UploadResponse upload(final MultipartFile file) throws IOException {
        final Map response =
                this.cloudinaryClient
                        .uploader()
                        .upload(this.convertMultiPartToFile(file), ObjectUtils.emptyMap());
        log.debug("cloudinary image upload response: [{}]", response);
        JSONObject json = new JSONObject(response);
        return new UploadResponse(
                json.getString("url"),
                json.getString("secure_url"),
                json.getString("format"),
                json.getInt("width"),
                json.getInt("height"),
                json.getInt("bytes"),
                json.getString("original_filename"),
                json.getString("created_at"));
    }

    private File convertMultiPartToFile(final MultipartFile file) throws IOException {
        final File newFile = Files.createTempFile("temp", file.getOriginalFilename()).toFile();
        file.transferTo(newFile);
        return newFile;
    }
}
