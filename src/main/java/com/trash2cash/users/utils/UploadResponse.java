package com.trash2cash.users.utils;

import lombok.*;

@Setter
@Getter
@Data
public class UploadResponse {

    private String url;
    private String secureUrl;
    private String format;
    private int width;
    private int height;
    private int bytes;
    private String originalFileName;
    private String createdAt;

    public UploadResponse() {}

    public UploadResponse(String url, String secureUrl, String format, int width, int height, int bytes, String originalFileName, String createdAt) {
        this.url = url;
        this.secureUrl = secureUrl;
        this.format = format;
        this.width = width;
        this.height = height;
        this.bytes = bytes;
        this.originalFileName = originalFileName;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UploadResponse{" +
                "url='" + url + '\'' +
                ", secureUrl='" + secureUrl + '\'' +
                ", format='" + format + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", bytes=" + bytes +
                ", originalFileName='" + originalFileName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}