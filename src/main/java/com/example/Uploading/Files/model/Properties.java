package com.example.Uploading.Files.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "file")
public class Properties {
    private String location;
}
