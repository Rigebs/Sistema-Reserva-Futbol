package com.cruz_sur.api.dto;

import lombok.Data;

@Data
public class PasswordResetDto {
    private String email;
    private String verificationCode;
    private String newPassword;

}