package local.hackaroni.ai.controllers;

import jakarta.servlet.http.HttpSession;
import local.hackaroni.ai.content.Chat;
import local.hackaroni.ai.content.ChatMessage;
import local.hackaroni.ai.content.ChatState;
import local.hackaroni.ai.content.ChatStorage;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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
        return getChatsForUser((String) session.getAttribute("userId"))
                .get(chatId)
                .getMessages();
    }

    @PostMapping("/new")
    public int createChat(HttpSession session) {
        List<Chat> chats = getChatsForUser((String) session.getAttribute("userId"));
        chats.add(new Chat("Chat " + chats.size()));
        return chats.size() - 1;
    }


    @PostMapping(value = "/send", consumes = "multipart/form-data")
    public ChatMessage sendMessage(
            @RequestParam(required = false) String message,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam int chatId,
            HttpSession session
    ) throws IOException {

        String userId = (String) session.getAttribute("userId");
        Chat chat = getChatsForUser(userId).get(chatId);
        ChatState state = chat.getState();
        String age = String.valueOf(session.getAttribute("age"));


        String userText = (file != null && !file.isEmpty())
                ? "[file] " + file.getOriginalFilename()
                : message;

        ChatMessage userMsg = new ChatMessage("user", userText);
        chat.getMessages().add(userMsg);

        if (state.field.isEmpty()) {
            state.field = message;
            state.age = age;
            String reply = sendToPythonFieldGuide(state.field, state.age);

            ChatMessage botMsg = new ChatMessage("bot", reply);
            chat.getMessages().add(botMsg);

            return botMsg;
        }

        if (!state.testStarted) {
            state.testStarted = true;
            state.guide = message;

            String reply = sendToPythonStartTest(
                    state.field,
                    state.guide,
                    state.age
            );

            ChatMessage botMsg = new ChatMessage("bot", reply);
            chat.getMessages().add(botMsg);

            return botMsg;
        }

        String reply = sendToPythonSmart(
                state.field,
                state.guide,
                state.age,
                message
        );

        ChatMessage botMsg = new ChatMessage("bot", reply);
        chat.getMessages().add(botMsg);

        return botMsg;
    }

    private String postToPython(String url, Map<String, Object> payload) {
        RestTemplate rest = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(payload, headers);
        Map<?, ?> res = rest.postForObject(url, req, Map.class);

        return res != null ? (String) res.get("answer") : "Python error";
    }

    private String sendToPythonFieldGuide(String field, String age) {
        Map<String, Object> p = new HashMap<>();
        p.put("field", field);
        p.put("age", age);
        return postToPython("http://127.0.0.1:8001/api/ask/field-guide", p);
    }

    private String sendToPythonStartTest(String field, String guide, String age) {
        Map<String, Object> p = new HashMap<>();
        p.put("theme", field);
        p.put("guide", guide);
        p.put("age", age);
        return postToPython("http://127.0.0.1:8001/api/ask/make_start_test", p);
    }

    private String sendToPythonSmart(String field, String guide, String age, String message) {
        Map<String, Object> p = new HashMap<>();
        p.put("field", field);
        p.put("guide", guide);
        p.put("age", age);
        p.put("message", message);
        return postToPython("http://127.0.0.1:8001/api/ask/smart", p);
    }

    private List<Chat> getChatsForUser(String userId) {
        ChatStorage.userChats.putIfAbsent(userId, new ArrayList<>(List.of(new Chat("Main chat"))));
        return ChatStorage.userChats.get(userId);
    }
}
