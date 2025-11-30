package local.hackaroni.ai.controllers;

import jakarta.servlet.http.HttpSession;
import local.hackaroni.ai.users.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) return "redirect:/chat";

        return "home/home";
    }
}
