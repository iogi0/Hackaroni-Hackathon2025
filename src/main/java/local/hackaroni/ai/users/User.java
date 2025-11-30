package local.hackaroni.ai.users;

public class User {
    private String username;
    private int age;
    private String email;
    private String password;

    public User() {}
    public User(String username,int age, String email, String password) {
        this.username = username;
        this.age = age;
        this.email = email;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

