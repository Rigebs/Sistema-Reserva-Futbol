package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Empresa;
import com.cruz_sur.api.responses.EmpresaClienteResponse;

import java.util.List;
import java.util.Optional;

public interface IEmpresaService {
    List<Empresa> all();
    Optional<Empresa> byId(Long id);
    Empresa update(Long id, Empresa Empresa);
    EmpresaClienteResponse save(Empresa Empresa);
    Empresa changeStatus(Long id, Integer status);
}
