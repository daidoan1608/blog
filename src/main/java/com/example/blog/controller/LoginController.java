package com.example.blog.controller;

import com.example.blog.dto.CommentDto;
import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.service.Impl.CommentServiceImpl;
import com.example.blog.service.Impl.PostServiceImpl;
import com.example.blog.service.Impl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserServiceImpl userServiceImpl;

    @GetMapping("/login")
    public String login() {
        log.info("Login page");
        return "login";
    }

    @GetMapping("/register")
    public String signUp() {
        return "sign-up";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("re_password") String rePassword,
                           RedirectAttributes redirectAttributes) {

        if (!password.equals(rePassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không trùng khớp.");
            log.error("Mật khẩu không trùng khớp.");
            return "redirect:/sign-up";
        }

        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setRole(User.Role.valueOf("USER"));

        if (userServiceImpl.create(userDto) == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tao tài khoản không thành công.");
            log.info("Tạo tài khoản không thành công.");
        }
        return "redirect:/sign-up";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}