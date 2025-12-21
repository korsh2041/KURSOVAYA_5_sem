package com.library.authentication;

import com.library.users.User;
import java.util.Map;
import java.util.HashMap;

public class SessionService {
    private Map<String, User> activeSessions = new HashMap<>();

    public String createSession(User user) {
        String sessionId = "SESSION_" + System.currentTimeMillis();
        activeSessions.put(sessionId, user);
        return sessionId;
    }

    public User getUserBySession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
    }

    public int getActiveSessionsCount() {
        return activeSessions.size();
    }
}