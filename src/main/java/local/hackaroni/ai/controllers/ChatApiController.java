package local.hackaroni.ai.controllers;

import jakarta.servlet.http.HttpSession;
import local.hackaroni.ai.content.Chat;
import local.hackaroni.ai.content.ChatMessage;
import local.hackaroni.ai.content.ChatStorage;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class ChatApiController {

    @GetMapping("/all")
    public List<Chat> getAllChats(HttpSession session) {
        return getChatsForUser((String) session.getAttribute("userId"));
    }

    @GetMapping("/history")
    public List<ChatMessage> getHistory(@RequestParam int chatId, HttpSession session) {
        return getChatsForUser((String) session.getAttribute("userId")).get(chatId).getMessages();
    }

    @PostMapping("/new")
    public int createChat(HttpSession session) {
        List<Chat> chats = getChatsForUser((String) session.getAttribute("userId"));
        chats.add(new Chat("Chat " + chats.size()));
        return chats.size() - 1;
    }
    @PostMapping(value = "/send", consumes = "multipart/form-data")
    public ChatMessage sendMessage(@RequestParam(required = false) String message, @RequestParam(required = false) MultipartFile file, @RequestParam int chatId, HttpSession session) throws IOException {
        String userId = (String) session.getAttribute("userId");
        List<Chat> chats = getChatsForUser(userId);
        Chat chat = chats.get(chatId);

        String userContent = message;
        if (file != null && !file.isEmpty()) {
            userContent = "[file] " + file.getOriginalFilename();
        }

        ChatMessage userMsg = new ChatMessage("user", userContent);
        chat.getMessages().add(userMsg);

        String pythonReply = sendToPython(message, file);

        ChatMessage botMsg = new ChatMessage("bot", pythonReply);
        chat.getMessages().add(botMsg);

        return botMsg;
    }

    private String sendToPython(String message, MultipartFile file) {
        RestTemplate rest = new RestTemplate();

        Map<String, String> payload = new HashMap<>();

        if (file != null && !file.isEmpty()) {
            payload.put("question", "FILE: " + file.getOriginalFilename());
        } else {
            payload.put("question", message != null ? message : "");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        Map<?, ?> response = rest.postForObject("http://127.0.0.1:8001/api/ask", request, Map.class);

        return response != null ? (String) response.get("answer") : "No return";
    }


    private List<Chat> getChatsForUser(String userId) {
        ChatStorage.userChats.putIfAbsent(userId, new ArrayList<>(List.of(new Chat("Main chat"))));
        return ChatStorage.userChats.get(userId);
    }
}
