package com.example.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.exception.RoleEmptyExcepcion;
import com.example.exception.RoleNoFoundException;
import com.example.exception.UserEmptyException;
import com.example.exception.UserNoFoundException;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.IUserRepository;
import com.example.service.interf.IRoleService;
import com.example.service.interf.IUserService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IRoleService roleService;
    
    @Override
    public void create(User userToAdd) throws UserEmptyException {
        
        if (userToAdd == null) throw new UserEmptyException("User information is empty, be sure to complete the fields");

        userRepository.save(userToAdd);

    }

    @Override
    public void update(User userToUpdate) throws UserNoFoundException, UserEmptyException {

        if (userToUpdate == null) throw new UserEmptyException("User information is empty, be sure to complete the fields");

        Optional<User> userSaved = userRepository.findById(userToUpdate.getId());

        if (userSaved.isEmpty()) throw new UserNoFoundException("User wasn't found in database");

        userRepository.save(userToUpdate);

    }

    @Override
    public void delete(User userToDelete) throws UserNoFoundException, UserEmptyException {

        if (userToDelete == null) throw new UserEmptyException("User information is empty, be sure to complete the fields");

        Optional<User> userSaved = userRepository.findById(userToDelete.getId());

        if (userSaved.isEmpty()) throw new UserNoFoundException("User wasn't found in database");

        userRepository.delete(userToDelete);
        
    }

    @Override
    public User findByName(String name) throws UserEmptyException, UserNoFoundException {

        if (name == null || name.trim().isEmpty()) throw new UserEmptyException("User information is empty, be sure to complete the fields");

        Optional<User> userFound = userRepository.findByName(name);

        if (userFound.isEmpty()) throw new UserNoFoundException("User didn't found in DB");
        
        return  userFound.get();

    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(int id) throws UserEmptyException, UserNoFoundException {

        if (id < 0) throw new UserEmptyException("User information is wrong, be sure to complete the fields");

        Optional<User> userFound = userRepository.findById(id);

        if (userFound.isEmpty()) throw new UserNoFoundException("User wasn't found in database");

        return userFound.get();

    }

    @Override
    public void assignRole(int userId, int roleId)
            throws UserEmptyException, UserNoFoundException, RoleEmptyExcepcion, RoleNoFoundException {

        User user = findById(userId);
        Role role = roleService.findById(roleId);

        user.setRole(role);
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);

    }

    @Override
    public void removeRole(int userId) throws UserEmptyException, UserNoFoundException {

        User user = findById(userId);

        user.setRole(null);
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);

    }

}
