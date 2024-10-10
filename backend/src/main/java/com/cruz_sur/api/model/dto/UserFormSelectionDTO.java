package com.cruz_sur.api.model.dto;

import com.cruz_sur.api.model.Cliente;
import com.cruz_sur.api.model.Sede;
import lombok.Data;

@Data
public class UserFormSelectionDTO {
    private String formType;
    private Long clienteId;
    private Long sedeId;

    public Cliente getCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        return cliente;
    }

    public Sede getSede() {
        Sede sede = new Sede();
        sede.setId(sedeId);
        return sede;
    }
}
