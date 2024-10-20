package com.cruz_sur.api.dto;

import lombok.Data;

@Data
public class UpdateClientAndSedeDto {
    private Long clienteId;
    private Long sedeId;

    // Constructor
    public UpdateClientAndSedeDto() {}

    public UpdateClientAndSedeDto(Long clienteId, Long sedeId) {
        this.clienteId = clienteId;
        this.sedeId = sedeId;
    }
}
