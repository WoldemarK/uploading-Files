package com.example.Uploading.Files;

import com.example.Uploading.Files.model.Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(Properties.class)
public class UploadingFilesApplication {
	public static void main(String[] args) {
		SpringApplication.run(UploadingFilesApplication.class, args);
	}

}
