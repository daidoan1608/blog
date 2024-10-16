package com.example.blog.controller;

import com.example.blog.model.User;
import com.example.blog.service.Impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserServiceImpl userServiceImpl;

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        // Xử lý đăng nhập tại đây
        // Kiểm tra thông tin đăng nhập
        User user = userServiceImpl.findByUsernameAndPassword(username, password);

        // Nếu thông tin đăng nhập không chính xác
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác.");
            return "redirect:/sign-in";
        }

        if (user.getRole().equals(User.Role.ADMIN)) {
            return "redirect:/admin/users";
        }

        if (user.getRole().equals(User.Role.USER)) {
            return "redirect:/";
        }

        // Nếu thông tin đăng nhập chính xác
        return "redirect:/";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("email") String email,
                        @RequestParam("password") String password,
                        @RequestParam("re_password") String rePassword,
                        RedirectAttributes redirectAttributes) {
        // Xử lý đăng nhập tại đây
        // Kiểm tra thông tin đăng nhập
        if(!password.equals(rePassword)){
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không trùng khớp.");
            return "redirect:/sign-up";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(User.Role.valueOf("USER"));

        // Nếu thông tin đăng nhập không chính xác
        if (userServiceImpl.create(user) == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tao tài khoản không thành công.");
        }
        // Nếu thông tin đăng nhập chính xác
        return "redirect:/sign-up";
    }
}
