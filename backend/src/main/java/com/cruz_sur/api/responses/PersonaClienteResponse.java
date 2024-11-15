package com.cruz_sur.api.responses;

import com.cruz_sur.api.model.Persona;
import com.cruz_sur.api.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonaClienteResponse {
    private Persona persona;
    private Cliente cliente;
}
