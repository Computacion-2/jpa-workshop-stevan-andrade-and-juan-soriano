package com.example.service;

import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado: " + id);
        }
        return user.get();
    }

    public User create(String name, String email, String password, Long roleId) {
        Role role = getRole(roleId);
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return userRepository.save(user);
    }

    public User update(Long id, String name, String email, String password, Long roleId) {
        User user = findById(id);
        Role role = getRole(roleId);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return userRepository.save(user);
    }

    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    private Role getRole(Long roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("El usuario debe tener un rol");
        }
        return roleService.findById(roleId);
    }
}
