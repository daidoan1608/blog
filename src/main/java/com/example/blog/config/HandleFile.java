package com.example.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;

@Slf4j
@Component
public class HandleFile {
    public String uploadAvatar(MultipartFile avatar, String username) throws IOException {
        // Đường dẫn đến thư mục img trong resources/static
        Path staticPath = Paths.get("uploads", "avatars", username);
        // Kiểm tra xem thư mục đã tồn tại chưa, nếu chưa thì tạo mới
        if (!Files.exists(staticPath)) {
            Files.createDirectories(staticPath);
        }
        // Tạo đường dẫn cho tệp ảnh
        Path file = staticPath.resolve(Objects.requireNonNull(avatar.getOriginalFilename()).replaceAll(" ", "_"));
        // Lưu tệp ảnh vào thư mục
        try (OutputStream os = Files.newOutputStream(file)) {
            os.write(avatar.getBytes());
        }
        // Trả về đường dẫn tương đối để lưu vào database
        return "/avatars/" + username + "/" + avatar.getOriginalFilename().replaceAll(" ", "_");
    }

    public void deleteFile(String path) throws IOException {
        Path folderPath = Paths.get(System.getProperty("user.dir"), "uploads", path).getParent();
        if (Files.exists(folderPath)) {
            Files.walk(folderPath).sorted(Comparator.reverseOrder())
                    .forEach(filePath -> {
                        try {
                            log.info("Đang xóa: " + filePath);
                            Files.delete(filePath);
                        } catch (IOException e) {
                            log.warn("Không thể xóa file: " + filePath);
                            log.error(e.getMessage());
                        }
                    });
        }
    }
}
