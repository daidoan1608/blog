package com.example.blog.controller;

import com.example.blog.dto.UserDto;
import com.example.blog.model.User;
import com.example.blog.service.Impl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/sign-in")
    public String signIn() {
        return "sign-in";
    }

    @GetMapping("/sign-up")
    public String signUp() {
        return "sign-up";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        RedirectAttributes redirectAttributes,
                        HttpSession session) {
        UserDto userDto = userServiceImpl.findByUsernameAndPassword(username, password);

        if (userDto == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Tên đăng nhập hoặc mật khẩu không chính xác.");
            return "redirect:/sign-in";
        }

        session.setAttribute("loggedInUser", userDto);

        if (userDto.getRole().equals(User.Role.ADMIN)) {
            log.info("Admin logged in");
            return "redirect:/admin/users";
        }

        if (userDto.getRole().equals(User.Role.USER)) {
            log.info("User logged in");
            return "redirect:/profile/" + userDto.getId();
        }

        return "redirect:/";
    }

    @GetMapping("/profile/{id}")
    public String profile(@PathVariable UUID id, Model model, HttpSession session) {
        // Lấy user từ session
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null || !loggedInUser.getId().equals(id)) {
            return "redirect:/sign-in";
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("message", "Thông tin cá nhân");

        return "profile";
    }


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Thông tin public");
        return "home";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("re_password") String rePassword,
                           RedirectAttributes redirectAttributes) {

        if(!password.equals(rePassword)){
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không trùng khớp.");
            return "redirect:/sign-up";
        }

        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setRole(User.Role.valueOf("USER"));

        if (userServiceImpl.create(userDto) == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tao tài khoản không thành công.");
        }
        return "redirect:/sign-up";
    }
}
