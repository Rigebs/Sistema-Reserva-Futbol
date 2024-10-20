package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Departamento;

import java.util.List;
import java.util.Optional;

public interface IDepartamentoService {
    List<Departamento> all();
    Optional<Departamento> byId(Long id );

}
