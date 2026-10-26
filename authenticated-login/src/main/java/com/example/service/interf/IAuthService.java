package com.example.service.interf;

import com.example.exception.RegistrationException;
import com.example.model.User;

public interface IAuthService {

    User register(String name, String email, String password, String confirmPassword) throws RegistrationException;

}
