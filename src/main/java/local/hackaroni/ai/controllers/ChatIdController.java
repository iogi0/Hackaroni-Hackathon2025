package local.hackaroni.ai.controllers;

import jakarta.servlet.http.HttpSession;
import local.hackaroni.ai.users.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatIdController {

    @GetMapping("/chat/{chatId}")
    public String chatById(@PathVariable int chatId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";


        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("chatId", chatId);

        return "content/chat_id";
    }
}
