package com.meldateksari.eticaret.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Boolean isActive;
    private String passwordHash;
    private String profileImageUrl;
}
