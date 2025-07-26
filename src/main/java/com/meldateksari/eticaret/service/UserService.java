package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.auth.dto.AuthResponse;
import com.meldateksari.eticaret.auth.dto.LoginRequestDto;
import com.meldateksari.eticaret.auth.dto.RegisterRequestDto;
import com.meldateksari.eticaret.auth.enums.Role;
import com.meldateksari.eticaret.auth.config.SecurityConfig;
import com.meldateksari.eticaret.auth.service.JwtService;
import com.meldateksari.eticaret.model.User;
import com.meldateksari.eticaret.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    user.setEmail(updatedUser.getEmail());
                    user.setPhoneNumber(updatedUser.getPhoneNumber());
                    user.setIsActive(updatedUser.getIsActive());

                    // Sadece yeni şifre gönderildiyse güncelle
                    if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isEmpty()) {
                        user.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
                    }

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    //kullanıcıya rol atama
    public User assignRoleToUser(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getRoles().add(role);
        return userRepository.save(user);
    }


    public void updatePassword(Long id, com.meldateksari.eticaret.auth.dto.UpdatePasswordRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("New password cannot be null or empty.");
        }
    }

    public AuthResponse register(RegisterRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);
        //TODO email zaten var hatası göster
        if (user == null) {
            user = new User();
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            user.setIsActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setRoles(Set.of(Role.ROLE_USER));
            createUser(user);
        }
        return login(LoginRequestDto.builder().email(dto.getEmail()).password(dto.getPassword()).build());

    }

    public AuthResponse login(LoginRequestDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);
        String token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).user(user).build();
    }


}
