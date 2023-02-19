package com.example.Uploading.Files.service;

import com.example.Uploading.Files.model.Properties;
import com.example.Uploading.Files.exception.FileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final Path location;

    @Autowired
    public FileServiceImpl(Properties properties) {
        this.location = Paths.get(properties.getLocation());
    }


    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(location);
        } catch (IOException ex) {
            throw new FileNotFoundException("Could not initialize storage location", ex);
        }
    }


    @Override
    public String multipartFile(MultipartFile file) {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (file.isEmpty()) {
                throw new FileNotFoundException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                throw new FileNotFoundException
                        (
                                "Cannot store file with relative path outside current directory "
                                        + filename
                        );
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.location.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new FileNotFoundException("Failed to store file " + filename, ex);
        }

        return filename;
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.location, 1)
                    .filter(path -> !path.equals(this.location))
                    .map(this.location::relativize);
        } catch (IOException ex) {
            throw new FileNotFoundException("Failed to read stored files", ex);
        }
    }

    @Override
    public Path load(String filename) {
        return location.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(location.toFile());
    }
}