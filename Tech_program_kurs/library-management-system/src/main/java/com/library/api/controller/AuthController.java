package com.library.api.controller;

import com.library.api.dto.LoginRequest;
import com.library.api.dto.LoginResponse;
import com.library.api.dto.UserDTO;
import com.library.persistence.LibraryRepository;
import com.library.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import com.library.api.dto.RegisterRequest;
import com.library.users.Student;
import com.library.users.Professor;
import com.library.users.Librarian;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final LibraryRepository repository;

    @Autowired
    public AuthController(LibraryRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            Optional<User> userOpt = repository.findUserByCredentials(request.getUsername(), request.getPassword());
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String sessionId = "SESSION_" + System.currentTimeMillis() + "_" + user.getUserId();
                
                UserDTO userDTO = new UserDTO(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getClass().getSimpleName()
                );
                
                return new LoginResponse(true, "Успешный вход", sessionId, userDTO);
            } else {
                return new LoginResponse(false, "Неверное имя пользователя или пароль");
            }
        } catch (Exception e) {
            return new LoginResponse(false, "Ошибка входа: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest request) {
        try {
            // Валидация входных данных
            if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
                return new LoginResponse(false, "ID пользователя не может быть пустым");
            }
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return new LoginResponse(false, "Имя пользователя не может быть пустым");
            }
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return new LoginResponse(false, "Пароль не может быть пустым");
            }
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return new LoginResponse(false, "Пароли не совпадают");
            }
            
            // Проверка, что пользователь не существует
            Optional<User> existingUser = repository.findUserById(request.getUserId());
            if (existingUser.isPresent()) {
                return new LoginResponse(false, "Пользователь с таким ID уже существует");
            }
            
            // Создание пользователя в зависимости от типа
            User newUser;
            String userType = request.getUserType() != null ? request.getUserType() : "student";
            
            switch (userType.toLowerCase()) {
                case "teacher":
                case "professor":
                    newUser = new Professor(
                        request.getUserId(),
                        request.getName(),
                        request.getEmail(),
                        request.getPhone(),
                        "",
                        ""
                    );
                    break;
                case "librarian":
                    newUser = new Librarian(
                        request.getUserId(),
                        request.getName(),
                        request.getEmail(),
                        request.getPhone(),
                        "",
                        ""
                    );
                    break;
                case "student":
                default:
                    newUser = new Student(
                        request.getUserId(),
                        request.getName(),
                        request.getEmail(),
                        request.getPhone(),
                        "",
                        ""
                    );
                    break;
            }
            
            // Сохранение пользователя с хешированием пароля
            repository.saveUser(newUser, request.getPassword());
            
            String sessionId = "SESSION_" + System.currentTimeMillis() + "_" + newUser.getUserId();
            UserDTO userDTO = new UserDTO(
                newUser.getUserId(),
                newUser.getName(),
                newUser.getEmail(),
                newUser.getPhone(),
                newUser.getClass().getSimpleName()
            );
            
            return new LoginResponse(true, "Регистрация успешна", sessionId, userDTO);
        } catch (Exception e) {
            return new LoginResponse(false, "Ошибка регистрации: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public LoginResponse logout(@RequestParam String sessionId) {
        try {
            return new LoginResponse(true, "Вы вышли из системы");
        } catch (Exception e) {
            return new LoginResponse(false, "Ошибка выхода: " + e.getMessage());
        }
    }

    @GetMapping("/check-session")
    public LoginResponse checkSession(@RequestParam String sessionId) {
        try {
            if (sessionId != null && !sessionId.isEmpty()) {
                return new LoginResponse(true, "Сессия активна");
            }
            return new LoginResponse(false, "Сессия неактивна");
        } catch (Exception e) {
            return new LoginResponse(false, "Ошибка проверки сессии");
        }
    }

}
