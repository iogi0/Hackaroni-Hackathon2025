package local.hackaroni.ai.service;

import local.hackaroni.ai.users.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();

    public UserService() {
        users.add(new User("dev", "dev@test.com", "1234"));
    }

    public boolean register(User user) {
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) return false;
        }
        users.add(user);
        return true;
    }

    public User login(String username, String password) {
        return users.stream().filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password)).findFirst().orElse(null);
    }
}
