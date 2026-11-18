package ru.yandex.practicum.gateway.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public class GatewayUsersProperties {

    private List<User> users = new ArrayList<>();

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Getter
    @Setter
    public static class User {
        private String username;
        private String password;
        private List<String> roles = new ArrayList<>();
    }
}
