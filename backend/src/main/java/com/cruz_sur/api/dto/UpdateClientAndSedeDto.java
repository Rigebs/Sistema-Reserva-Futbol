package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClientAndSedeDto {
    private Long clienteId;
    private Long companiaId;

    // Getters y setters
}

