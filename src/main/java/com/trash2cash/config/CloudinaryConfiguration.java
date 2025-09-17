package com.trash2cash.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@Configuration
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryConfiguration {

    private String apiKey;
    private String apiSecret;
    private String cloudName;

}
