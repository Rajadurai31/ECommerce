package com.ecommerce.service;

import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.entity.User;
import org.springframework.security.core.userdetails.*;
import org.springframework.crypto.password.PasswordEncoder;

import javax.management.RuntimeErrorException;
import java.util.Optional;
import java.util.List;

@Service
@Transactional

public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

     public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
     }
     public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
     }
     public List<User> findAll() {
        return userRepository.findAll();
     }
     public void delete(Long id) {
        userRepository.deleteById(id);
     }

     public User createUser(User user) {
        if(userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
     }

     public User updateUser(Long id ,User updatedUser) {
        User existingUser = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        if(existingUser.getUsername() != updatedUser.getUsername() && userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if(existingUser.getEmail() != updatedUser.getEmail() && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        if(!updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return userRepository.save(existingUser);
     }
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + identifier));
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
    }


}