package com.example.blog.controller;

import com.example.blog.dto.UserDto;
import com.example.blog.model.User;
import com.example.blog.service.Impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.BindingResult;

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
@SessionAttributes("user")
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userServiceImpl;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<UserDto> users = userServiceImpl.getAll();
        model.addAttribute("users", users);
        return "admin";  // Trả về tên của file HTML
    }

    @GetMapping("/users/new")
    public String showCreateForm(Model model) {
        UserDto userDto = new UserDto();// Khởi tạo một UserDto mới
        MultipartFile avatar = null;
        model.addAttribute("userDto", userDto); // Thêm vào mô hình
        return "create-form"; // Tên template cho biểu mẫu
    }


    @PostMapping("/users/new")
    public String create(@Valid @ModelAttribute UserDto userDto,
                         BindingResult bindingResult,
                         @RequestParam("img") MultipartFile img) throws IOException {
        // Kiểm tra lỗi trước khi thực hiện bất kỳ thao tác nào
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.getDefaultMessage());
            });
            return "create-form"; // Trở lại form nếu có lỗi
        }

        // Lưu tệp avatar và lấy đường dẫn
        String avatarPath = uploadAvatar(img, userDto.getUsername());
        userDto.setAvatar(avatarPath); // Cập nhật đường dẫn vào UserDto

        // Lưu người dùng
        userServiceImpl.create(userDto);

        return "redirect:/admin/users";
    }
    @GetMapping("/users/update/{id}")
    public String showFormUpdate(@PathVariable UUID id, Model model) {
        UserDto userDto = userServiceImpl.findById(id);
        model.addAttribute("user", userDto);
        return "update-form";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@Valid @ModelAttribute UserDto userDto,
                             BindingResult bindingResult,
                             @RequestParam(value = "img", required = false) MultipartFile img) throws IOException {
        // Lấy người dùng từ cơ sở dữ liệu theo ID
        UserDto existingUserDto = userServiceImpl.findById(userDto.getId());
        userDto.setPassword(existingUserDto.getPassword());
        // Kiểm tra lỗi trước khi thực hiện bất kỳ thao tác nào
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.getDefaultMessage());
            });
            return "update-form"; // Trở lại form nếu có lỗi
        }

        String oldAvatarPath = existingUserDto.getAvatar();

        // Kiểm tra nếu có file mới được upload
        if (img != null && !img.isEmpty()) {
            // Xóa tệp avatar cũ
            deleteFile(oldAvatarPath);

            // Cập nhật avatar mới
            String newAvatarPath = uploadAvatar(img, userDto.getUsername());
            userDto.setAvatar(newAvatarPath); // Cập nhật đường dẫn avatar mới
        } else {
            // Nếu không có tệp mới, giữ nguyên đường dẫn cũ
            userDto.setAvatar(oldAvatarPath);
        }

        userServiceImpl.update(userDto.getId(), userDto);

        return "redirect:/admin/users";
    }


    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable UUID id) throws IOException {
        UserDto userDto = userServiceImpl.findById(id);
        String avatarPath = userDto.getAvatar();

        // Kiểm tra nếu avatarPath không rỗng và xóa thư mục chứa ảnh
        if (avatarPath != null && !avatarPath.isEmpty()) {
            // Chuyển avatarPath thành đường dẫn thực trên hệ thống file
            Path folderPath = Paths.get(System.getProperty("user.dir"), "uploads", avatarPath).getParent();
            deleteFile(avatarPath);
        }
        userServiceImpl.deleteById(id);
        return "redirect:/admin/users";
    }

    public String uploadAvatar(MultipartFile avatar, String username) throws IOException {
        // Đường dẫn đến thư mục img trong resources/static
        Path staticPath = Paths.get("uploads", "avatars", username);
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
        return "/avatars/" + username + "/" + avatar.getOriginalFilename().replaceAll(" ", "_");
    }

    public void deleteFile(String path) throws IOException {
        Path folderPath = Paths.get(System.getProperty("user.dir"), "uploads", path).getParent();
        if (Files.exists(folderPath)) {
            Files.walk(folderPath).sorted(Comparator.reverseOrder())
                    .forEach(filePath -> {
                        try {
                            System.out.println("Đang xóa: " + filePath);
                            Files.delete(filePath);
                        } catch (IOException e) {
                            log.info("Không thể xóa file: " + filePath);
                            e.printStackTrace();
                        }
                    });
        }
    }
}
