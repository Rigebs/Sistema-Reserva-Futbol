package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Provincia;

import java.util.List;
import java.util.Optional;

public interface IProvinciaService {
    List<Provincia> all();
    Optional<Provincia> byId(Long id);
    List<Provincia> findDepartamentoById(Long departamentoId);
}
