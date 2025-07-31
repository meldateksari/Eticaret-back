// src/main/java/com/meldateksari/eticaret/security/CustomUserDetails.java

package com.meldateksari.eticaret.security;

import com.meldateksari.eticaret.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final Long id;
    private final String username; // Bu alan email'i temsil edebilir
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getEmail(); // Kullanıcı adı olarak email'i kullanıyoruz
        this.password = user.getPasswordHash(); // Hashlenmiş şifreyi kullanıyoruz
        this.authorities = Collections.emptyList(); // Rolleriniz yoksa boş liste döndürebilirsiniz
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}