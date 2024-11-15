package com.cruz_sur.api.responses;

import com.cruz_sur.api.model.Empresa;
import com.cruz_sur.api.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmpresaClienteResponse {
    private Empresa empresa;
    private Cliente cliente;
}
