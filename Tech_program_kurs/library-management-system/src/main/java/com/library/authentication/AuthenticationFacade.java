package com.library.authentication;

import com.library.users.User;
import com.library.users.Student;
import com.library.users.Professor;
import com.library.users.Librarian;
import java.util.Map;
import java.util.HashMap;

public class AuthenticationFacade {
    private UserService userService;
    private PasswordService passwordService;
    private SessionService sessionService;
    private PermissionService permissionService;

    public AuthenticationFacade() {
        this.userService = new UserService();
        this.passwordService = new PasswordService();
        this.sessionService = new SessionService();
        this.permissionService = new PermissionService();
        initializeTestUsers();
    }

    private void initializeTestUsers() {
        Student student = new Student("STU001", "Иван Петров", "ivan@university.edu",
                "123-4567", "S12345", "Computer Science");
        Professor professor = new Professor("PROF001", "Доктор Смирнов", "smith@university.edu",
                "765-4321", "P987", "Engineering");
        Librarian librarian = new Librarian("LIB001", "Анна Сидорова", "anna@library.edu",
                "555-1234", "L001", "Утренняя");

        userService.registerUser(student);
        userService.registerUser(professor);
        userService.registerUser(librarian);
    }

    public String login(String username, String password) {
        System.out.println("Попытка входа для пользователя: " + username);

        User user = userService.findUserByUsername(username);
        if (user == null) {
            System.out.println("Пользователь не найден");
            return null;
        }

        if (passwordService.validatePassword(password, "default123")) {
            String sessionId = sessionService.createSession(user);
            System.out.println("Успешный вход. Создана сессия: " + sessionId);
            return sessionId;
        } else {
            System.out.println("Неверный пароль");
            return null;
        }
    }

    public String createSessionForUser(User user) {
        String sessionId = sessionService.createSession(user);
        System.out.println("Создана сессия: " + sessionId + " для пользователя: " + user.getName());
        return sessionId;
    }

    public void logout(String sessionId) {
        sessionService.invalidateSession(sessionId);
        System.out.println("Сессия завершена: " + sessionId);
    }

    public User getCurrentUser(String sessionId) {
        return sessionService.getUserBySession(sessionId);
    }

    public boolean checkPermission(String sessionId, String permission) {
        User user = getCurrentUser(sessionId);
        if (user == null) {
            return false;
        }
        return permissionService.hasPermission(user, permission);
    }

    public void registerNewUser(User user, String password) {
        userService.registerUser(user);
        System.out.println("Зарегистрирован новый пользователь: " + user.getName());
    }
}
