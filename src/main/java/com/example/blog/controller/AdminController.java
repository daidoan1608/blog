package com.example.blog.controller;

import com.example.blog.config.HandleFile;
import com.example.blog.dto.UserDto;
import com.example.blog.model.User;
import com.example.blog.service.Impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@SessionAttributes("user")
@RequestMapping("/admin")
public class AdminController {
    private final UserServiceImpl userServiceImpl;
    private final HandleFile handleFile;

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
        String avatarPath = handleFile.uploadAvatar(img, userDto.getUsername());
        userDto.setAvatar(avatarPath); // Cập nhật đường dẫn vào UserDto

        // Lưu người dùng
        userServiceImpl.create(userDto);

        return "redirect:/admin/users";
    }
    @GetMapping("/users/update/{id}")
    public String showFormUpdate(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable UUID id,
                                 Model model) {
        boolean isUser = false;
        User.Role role = User.Role.valueOf(userDetails.getAuthorities().toArray()[0].toString());
        if (!role.equals(User.Role.ADMIN)) {
            isUser = true;
        }
        UserDto userDto = userServiceImpl.findById(id);
        model.addAttribute("user", userDto);
        model.addAttribute("isUser", isUser);
        return "update-form";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@Valid @ModelAttribute UserDto userDto,
                             BindingResult bindingResult,
                             @RequestParam(value = "img", required = false) MultipartFile img) throws IOException {
        UserDto existingUserDto = userServiceImpl.findById(userDto.getId());
        userDto.setPassword(existingUserDto.getPassword());
        // Kiểm tra lỗi trước khi thực hiện bất kỳ thao tác nào
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                log.error(error.getDefaultMessage());
            });
            return "update-form";
        }

        String oldAvatarPath = existingUserDto.getAvatar();

        if (img != null && !img.isEmpty()) {
            handleFile.deleteFile(oldAvatarPath);

            String newAvatarPath = handleFile.uploadAvatar(img, userDto.getUsername());
            userDto.setAvatar(newAvatarPath);
        } else {
            userDto.setAvatar(oldAvatarPath);
        }

        userServiceImpl.update(userDto.getId(), userDto);

        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable UUID id) throws IOException {
        UserDto userDto = userServiceImpl.findById(id);
        String avatarPath = userDto.getAvatar();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            Path folderPath = Paths.get(System.getProperty("user.dir"), "uploads", avatarPath).getParent();
            handleFile.deleteFile(avatarPath);
        }
        userServiceImpl.deleteById(id);
        return "redirect:/admin/users";
    }
}
