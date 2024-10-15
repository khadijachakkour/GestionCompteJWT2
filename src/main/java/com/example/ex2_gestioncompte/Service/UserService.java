package com.example.ex2_gestioncompte.Service;


import com.example.ex2_gestioncompte.DTO.RegisterDto;
import com.example.ex2_gestioncompte.Entities.UserEntity;
import com.example.ex2_gestioncompte.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterDto registerDto) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("L'utilisateur existe déjà");
        }

        // Créer un nouvel utilisateur
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(registerDto.getUsername());
        userEntity.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userEntity.setAuthorities(registerDto.getAuthorities());

        // Enregistrer l'utilisateur dans la base de données
        userRepository.save(userEntity);
    }
    public UserEntity save(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }
}

