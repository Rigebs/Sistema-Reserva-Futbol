package com.cruz_sur.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDto {
    private String identifier;
    private String password;
}