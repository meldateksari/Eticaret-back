// src/main/java/com/meldateksari/eticaret/security/CustomUserDetailsService.java

package com.meldateksari.eticaret.security;

import com.meldateksari.eticaret.model.User;
import com.meldateksari.eticaret.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Kullanıcıyı veritabanından e-posta adresine göre bul
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));

        // Veritabanından gelen User nesnesini CustomUserDetails'e dönüştür
        return new CustomUserDetails(user);
    }
}