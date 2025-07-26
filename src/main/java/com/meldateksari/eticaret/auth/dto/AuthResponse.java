package com.meldateksari.eticaret.auth.dto;

import com.meldateksari.eticaret.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private String token;
    private User user;
}
