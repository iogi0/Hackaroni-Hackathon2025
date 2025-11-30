package local.hackaroni.ai.controllers;

import jakarta.servlet.http.HttpSession;
import local.hackaroni.ai.service.UserService;
import local.hackaroni.ai.users.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage() {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String register(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
        boolean ok = userService.register(new User(username, email, password));
        if (!ok) {
            model.addAttribute("error", "Username already exists");
            return "auth/registration";
        }

        model.addAttribute("success", "Account created successfully!");
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        User user = userService.login(username, password);

        if (user == null) {
            model.addAttribute("error", "Invalid credentials");
            return "auth/login";
        }

        session.setAttribute("user", user);
        session.setAttribute("userId", user.getUsername());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("email", user.getEmail());

        return "redirect:/chat";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
