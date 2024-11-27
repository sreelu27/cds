package com.sree.cds.service.impl;

import com.sree.cds.entity.User;
import com.sree.cds.entity.UserModel;
import com.sree.cds.repository.RoleRepository;
import com.sree.cds.repository.UserRepository;
import com.sree.cds.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserModel user1) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(user1.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user1.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create a new user object
        User user = new User();
        user.setUsername(user1.getUsername());
        user.setPassword(passwordEncoder.encode(user1.getPassword()));
        user.setEmail(user1.getEmail());
        user.setRoles(Set.of(roleRepository.findByName("ROLE_USER")));
        return userRepository.save(user);
    }

}

