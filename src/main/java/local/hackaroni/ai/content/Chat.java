package local.hackaroni.ai.content;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private String title;
    private List<ChatMessage> messages = new ArrayList<>();
    private ChatState state = new ChatState();

    public Chat(String title) {
        this.title = title;
    }

    public String getTitle() { return title; }
    public List<ChatMessage> getMessages() { return messages; }
    public ChatState getState() { return state; }
}

