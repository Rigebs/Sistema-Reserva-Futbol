package com.cruz_sur.api.dto;

import com.cruz_sur.api.model.Cliente;
import com.cruz_sur.api.model.Compania;
import com.cruz_sur.api.model.Empresa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {
    private Long userId;
    private String username;
    private String email;
    private String roles;

    private Compania compania;

    private Empresa empresa;

    private Cliente cliente;
}
