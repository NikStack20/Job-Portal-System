package com.ncst.job.portal.service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}") // default folder name if not set
    private String uploadDir;

    private Path root;

    @PostConstruct
    public void init() {
        try {
            root = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(root)) {
                Files.createDirectories(root);
                System.out.println("Created upload directory: " + root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    public String store(MultipartFile file, String ownerId) {
        String originalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String ext = "";
        int i = originalName.lastIndexOf('.');
        if (i >= 0) ext = originalName.substring(i);

        String filename = ownerId + "_" + UUID.randomUUID() + ext;
        Path targetLocation = root.resolve(filename);

        try {
            if (file.isEmpty()) throw new IOException("Empty file upload not allowed");
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            // Return file path (relative for front-end use)
            return "/files/resumes/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path filePath = root.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed file URL: " + filename, e);
        }
    }
}

