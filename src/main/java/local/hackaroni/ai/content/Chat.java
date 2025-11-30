package local.hackaroni.ai.content;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private String title;
    private List<ChatMessage> messages = new ArrayList<>();

    public Chat(String title){
        this.title = title;
    }


    public String getTitle() { return title; }
    public List<ChatMessage> getMessages() { return messages; }
}
