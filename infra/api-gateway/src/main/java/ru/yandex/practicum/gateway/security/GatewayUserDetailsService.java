package ru.yandex.practicum.gateway.security;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class GatewayUserDetailsService implements ReactiveUserDetailsService {

    private final Map<String, UserDetails> userMap;

    public GatewayUserDetailsService(GatewayUsersProperties usersProperties, PasswordEncoder passwordEncoder) {
        userMap = new HashMap<>();
        for (GatewayUsersProperties.User user : usersProperties.getUsers()) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            var authorities = user.getRoles().stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .toList();
            userMap.put(user.getUsername(),
                User.builder()
                    .username(user.getUsername())
                    .password(encodedPassword)
                    .authorities(authorities.toArray(new String[0]))
                    .build());
        }
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.justOrEmpty(userMap.get(username));
    }
}
