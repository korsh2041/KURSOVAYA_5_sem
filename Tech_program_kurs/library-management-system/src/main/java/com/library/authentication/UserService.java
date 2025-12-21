package com.library.authentication;

import com.library.users.User;
import java.util.Map;
import java.util.HashMap;

public class UserService {
    private Map<String, User> users = new HashMap<>();

    public User findUserByUsername(String username) {
        return users.get(username.toLowerCase());
    }

    public void registerUser(User user) {
        users.put(user.getName().toLowerCase(), user);
    }

    public boolean isUserExists(String username) {
        return users.containsKey(username.toLowerCase());
    }

    public Map<String, User> getAllUsers() {
        return new HashMap<>(users);
    }
}