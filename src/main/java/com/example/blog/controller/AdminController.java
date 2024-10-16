package com.example.blog.controller;

import com.example.blog.model.User;
import com.example.blog.service.Impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userServiceImpl;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userServiceImpl.getAll();
        model.addAttribute("users", users);
        return "admin";  // Trả về tên của file HTML
    }

    @GetMapping("/users/new")
    public String createUser() {
        return "crearte-form";
    }

    @PostMapping("/users/new")
    public String create(@RequestParam("username") String username,
                       @RequestParam("email") String email,
                       @RequestParam("fullname") String fullname,
                       @RequestParam("phone") String phone,
                       @RequestParam("role") String role,
                       @RequestParam("avatar") MultipartFile avatar) throws IOException {

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setFullname(fullname);
        user.setPhone(phone);
        user.setRole(User.Role.valueOf(role));
        user.setPassword("123456");
        user.setAvatar(uploadAvatar(avatar, username));
        userServiceImpl.create(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/update/{id}")
    public String showFormUpdate(@PathVariable UUID id, Model model) {
        User user = userServiceImpl.findById(id);
        model.addAttribute("user", user);
        return "update-form";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable("id") UUID id,
                             @RequestParam("username") String username,
                             @RequestParam("email") String email,
                             @RequestParam("fullname") String fullname,
                             @RequestParam("phone") String phone,
                             @RequestParam("role") String role,
                             @RequestParam("avatar") MultipartFile avatar) throws IOException {
        // Lấy người dùng từ cơ sở dữ liệu theo ID
        User user = userServiceImpl.findById(id);

        if (user == null) {
            return "redirect:/admin/users?error=UserNotFound";
        }

        // Cập nhật thông tin từ form
        user.setEmail(email);
        user.setUsername(username);
        user.setFullname(fullname);
        user.setPhone(phone);
        user.setRole(User.Role.valueOf(role));

        // Kiểm tra nếu avatar được upload
        user.setAvatar(uploadAvatar(avatar, username));

        // Cập nhật thông tin người dùng trong database
        userServiceImpl.update(id, user);

        return "redirect:/admin/users";
    }


    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable UUID id) throws IOException {
        User user = userServiceImpl.findById(id);
        String avatarPath = user.getAvatar();

        // Kiểm tra nếu avatarPath không rỗng và xóa thư mục chứa ảnh
        if (avatarPath != null && !avatarPath.isEmpty()) {
            // Chuyển avatarPath thành đường dẫn thực trên hệ thống file
            Path folderPath = Paths.get("src", "main", "resources", "static").resolve(avatarPath).getParent();

            // In ra đường dẫn để kiểm tra xem nó có đúng không
            System.out.println("Đường dẫn folder: " + folderPath.toAbsolutePath());

            // Kiểm tra xem thư mục có tồn tại không và xóa nó
            if (Files.exists(folderPath)) {
                System.out.println("Thư mục tồn tại, bắt đầu xóa...");
                // Xóa toàn bộ thư mục và các file bên trong
                Files.walk(folderPath)
                        .sorted(Comparator.reverseOrder()) // Đảm bảo xóa file trước khi xóa thư mục
                        .forEach(filePath -> {
                            try {
                                System.out.println("Đang xóa: " + filePath);
                                Files.delete(filePath);
                            } catch (IOException e) {
                                e.printStackTrace(); // Log lỗi nếu không xóa được
                            }
                        });
            } else {
                System.out.println("Thư mục không tồn tại!");
            }
        }

        // Xóa người dùng khỏi cơ sở dữ liệu
        userServiceImpl.deleteById(id);

        return "redirect:/admin/users";
    }

    public String uploadAvatar(MultipartFile avatar, String username) throws IOException {
        // Đường dẫn đến thư mục img trong resources/static
        Path staticPath = Paths.get("src", "main", "resources", "static", "img", "avatars", username);

        // Kiểm tra xem thư mục đã tồn tại chưa, nếu chưa thì tạo mới
        if (!Files.exists(staticPath)) {
            Files.createDirectories(staticPath);
        }

        // Tạo đường dẫn cho tệp ảnh
        Path file = staticPath.resolve(avatar.getOriginalFilename().replaceAll(" ", "_"));

        // Lưu tệp ảnh vào thư mục
        try (OutputStream os = Files.newOutputStream(file)) {
            os.write(avatar.getBytes());
        }

        // Trả về đường dẫn tương đối để lưu vào database
        return "/img/avatars/" + username + "/" + avatar.getOriginalFilename();
    }

}
