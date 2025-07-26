package com.meldateksari.eticaret.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class LoginRequestDto implements Serializable {
    private String email;
    private String password;
}
