package com.example.service.impl;

import com.example.dto.UserRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.mapper.IUserMapper;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.interf.IRoleService;
import com.example.service.interf.IUserService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor 
public class UserService implements IUserService{

    public static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final IRoleService roleService;
    private final IUserMapper userMapper;

    @Override 
    public List<UserResponseDTO> findAll() {

        List<User> users = userRepository.findAll();
        List<UserResponseDTO> userResponseDTOs = new ArrayList<UserResponseDTO>();

        if (users.isEmpty())
            for (User user : users) userResponseDTOs.add(userMapper.toResponse(user)); 

        return userResponseDTOs;

    }

    @Override 
    public UserResponseDTO findById(Long id) {

        if (id < 0) throw new IllegalArgumentException();
        Optional<User> user = userRepository.findById(id);

        return userMapper.toResponse(user
            .orElseThrow(() -> new IllegalArgumentException()));

    }

    private User findByIdDB(Long id) {

        if (id < 0) throw new IllegalArgumentException();
        Optional<User> user = userRepository.findById(id);

        return user.orElseThrow(() -> new IllegalArgumentException());

    }

    @Override 
    public UserResponseDTO create(UserRequestDTO userRequestDTO) {

        if (userRequestDTO == null) throw new IllegalArgumentException(); 

        User user = userMapper.toEntity(userRequestDTO);
        Role roleUser = getRole(userRequestDTO.getRoleId());
        user.setRole(roleUser);
        return userMapper.toResponse(userRepository.save(user));

    }

    @Override 
    public UserResponseDTO update(Long id, UserRequestDTO userRequestDTO) {

        if (userRequestDTO == null) throw new IllegalArgumentException();

        findByIdDB(id);
        User user = userMapper.toEntity(userRequestDTO);
        Role role = getRole(userRequestDTO.getRoleId());
        user.setRole(role);

        return userMapper.toResponse(userRepository.save(user));

    }

    @Override 
    public UserResponseDTO changeRole(Long userId, Long roleId) {

        User user = findByIdDB(userId);
        user.setRole(getRole(roleId));
        return userMapper.toResponse(userRepository.save(user));

    }

    // un usuario nunca queda sin rol: al quitarle el suyo vuelve al rol básico
    @Override 
    public UserResponseDTO removeRole(Long userId) {

        User user = findByIdDB(userId);
        Role defaultRole = roleService.findByNameDB(DEFAULT_ROLE);
        if (user.getRole().getId().equals(defaultRole.getId())) 
            throw new IllegalArgumentException("El usuario ya tiene solo el rol básico " + DEFAULT_ROLE);
        
        user.setRole(defaultRole);
        return userMapper.toResponse(userRepository.save(user));

    }

    @Override 
    public UserResponseDTO delete(Long id) {

        if (id < 0) throw new IllegalArgumentException();

        User user = findByIdDB(id);
        userRepository.delete(user);

        return userMapper.toResponse(user);

    }

    private Role getRole(Long roleId) {

        if (roleId == null) 
            throw new IllegalArgumentException("El usuario debe tener un rol");
        
        return roleService.findByIdDB(roleId);

    }
}
