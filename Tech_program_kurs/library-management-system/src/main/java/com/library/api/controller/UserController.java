package com.library.api.controller;

import com.library.api.dto.UserDTO;
import com.library.persistence.LibraryRepository;
import com.library.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final LibraryRepository repository;

    @Autowired
    public UserController(LibraryRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable String userId) {
        try {
            Optional<User> userOpt = repository.findUserById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return convertToDTO(user);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        try {
            return repository.findAllUsers().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/search/name")
    public List<UserDTO> searchByName(@RequestParam String query) {
        try {
            return repository.findAllUsers().stream()
                    .filter(u -> u.getName().toLowerCase().contains(query.toLowerCase()))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getClass().getSimpleName()
        );
    }
}
