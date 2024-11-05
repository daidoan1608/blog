package com.example.blog.controller;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserDto;
import com.example.blog.model.Post;
import com.example.blog.model.User;
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
    private final PostServiceImpl postServiceImpl;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        log.info("Login page");
        return "login";
    }

//    @PostMapping("/login")
//    public String login(@RequestParam("username") String username,
//                        @RequestParam("password") String password,
//                        HttpSession session,
//                        Model model) {
//        log.warn("Attempting to login user: " + username);
//        try {
//            // Tải userDetails từ service
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            String role = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse(null);
//            log.warn("Role: " + role);
//
//            if (passwordEncoder.matches(password, userDetails.getPassword())) {
//                // Tạo Authentication và lưu vào SecurityContext
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                        userDetails, password, userDetails.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                // Lấy role từ authorities
//
//                // Kiểm tra role
//                if ("ADMIN".equals(role)) {
//                    session.setAttribute("loggedInUser", userServiceImpl.findByUsername(username));
//                    return "redirect:/admin/users";
//                } else {
//                    session.setAttribute("loggedInUser", userServiceImpl.findByUsername(username));
//                    return "redirect:/"; // Điều hướng đến trang chủ
//                }
//            }
//        } catch (UsernameNotFoundException e) {
//            log.error("User not found", e);
//        }
//
//        model.addAttribute("errorMessage", "Invalid username or password");
//        return "login"; // Quay lại trang login nếu không thành công
//    }

    @GetMapping("/register")
    public String signUp() {
        return "sign-up";
    }

//    @GetMapping("/profile/{id}")
//    public String profile(@PathVariable UUID id, Model model, HttpSession session) {
//        User loggedInUser = (User) session.getAttribute("loggedInUser");
//
//        if (loggedInUser == null || !loggedInUser.getId().equals(id)) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("user", loggedInUser);
//        model.addAttribute("message", "Thông tin cá nhân");
//
//        return "profile";
//    }

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            UserDto userDto = userServiceImpl.findByUsername(username);
            if (userDto != null) {
                model.addAttribute("userId", userDto.getId());
                model.addAttribute("message", "Thông tin public");
            } else {
                model.addAttribute("message", "Người dùng không tồn tại.");
            }
        }
        List<PostDto> posts = postServiceImpl.getAllPosts();
        model.addAttribute("posts", posts);
        return "home";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("re_password") String rePassword,
                           RedirectAttributes redirectAttributes) {

        if (!password.equals(rePassword)) {
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Xóa thông tin người dùng khỏi session
        session.invalidate(); // Hủy session để đăng xuất

        // Xóa thông tin xác thực khỏi SecurityContextHolder
        SecurityContextHolder.clearContext();

        return "redirect:/login"; // Chuyển hướng về trang đăng nhập sau khi đăng xuất
    }
}