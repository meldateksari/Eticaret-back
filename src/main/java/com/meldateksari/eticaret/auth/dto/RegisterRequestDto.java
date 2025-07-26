package com.meldateksari.eticaret.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RegisterRequestDto implements Serializable {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
