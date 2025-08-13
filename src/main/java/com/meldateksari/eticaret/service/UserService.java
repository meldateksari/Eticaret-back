package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.auth.dto.AuthResponse;
import com.meldateksari.eticaret.auth.dto.LoginRequestDto;
import com.meldateksari.eticaret.auth.dto.RegisterRequestDto;
import com.meldateksari.eticaret.auth.dto.UpdatePasswordRequestDto;
import com.meldateksari.eticaret.auth.enums.Role;
import com.meldateksari.eticaret.auth.config.SecurityConfig;
import com.meldateksari.eticaret.auth.service.JwtService;
import com.meldateksari.eticaret.model.User;
import com.meldateksari.eticaret.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    @PersistenceContext
    private EntityManager em;

    @Getter
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
        return userRepository.findById(id).map(user -> {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEmail(updatedUser.getEmail());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            user.setIsActive(updatedUser.getIsActive());
            user.setImage(imageSet(user));
            // Sadece yeni şifre gönderildiyse güncelle
            if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isEmpty()) {
                user.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }
    @Transactional
    public void deleteUser(Long userId) {
        // 0) Kullanıcıyı yükle (remove için managed olmalı)
        User user = em.find(User.class, userId);
        if (user==null) {
            throw new ResponseStatusException(NOT_FOUND, "User not found");
        }

        // 1) Kullanıcının adres ID'leri
        var addressIds = em.createQuery(
                        "select a.id from Address a where a.user.id = :uid", Long.class)
                .setParameter("uid", userId)
                .getResultList();

        // 2) Bu adreslere bağlı siparişlerde FK'ları NULL'a çek
        if (!addressIds.isEmpty()) {
            em.createQuery(
                            "update Order o set o.billingAddress = null where o.billingAddress.id in :ids")
                    .setParameter("ids", addressIds)
                    .executeUpdate();

            em.createQuery(
                            "update Order o set o.shippingAddress = null where o.shippingAddress.id in :ids")
                    .setParameter("ids", addressIds)
                    .executeUpdate();

            // 3) Adresleri sil
            em.createQuery("delete from Address a where a.id in :ids")
                    .setParameter("ids", addressIds)
                    .executeUpdate();
        }

        // 4) Roller ilişkisinin temizlenmesi
        //    - @ElementCollection(Set<Role>) veya @ManyToMany(Role) fark etmez: clear() + flush() güvenlidir
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            user.getRoles().clear();
            em.flush(); // join/collection tablosu temizlensin
        }

        // 5) Kullanıcıyı sil
        em.remove(user);
    }

    @Transactional
    public User assignRoleToUser(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRoles() == null) user.setRoles(new HashSet<>());

        // TEK ROL politikası
        if (!(user.getRoles().size() == 1 && user.getRoles().contains(newRole))) {
            user.getRoles().clear();        // <-- önce hepsini sil
            user.getRoles().add(newRole);   // <-- sonra yeni rolü ekle
            user = userRepository.save(user);
        }
        return user;
    }


    public void updatePassword(Long id, UpdatePasswordRequestDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id " + id));

        // 1. currentPassword kontrolü
        if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isEmpty()) {
            throw new IllegalArgumentException("Current password cannot be empty.");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        // 2. newPassword kontrolü
        if (dto.getNewPassword() == null || dto.getNewPassword().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty.");
        }

        // 3. Şifreyi güncelle
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
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
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);
        user.setImage(imageSet(user));
        String token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).user(user).build();
    }

    private byte[] imageSet(User user) {
        String relativePath = user.getProfileImageUrl();  // Örnek: /uploads/profile-images/xyz.jpg
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }

        // Temel klasörü tanımlayın
        String fullPath = relativePath.replace("/", "\\");

        File file = new File(fullPath);
        if (!file.exists()) {
            System.out.println("Dosya bulunamadı: " + fullPath);
            return null;
        }

        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
