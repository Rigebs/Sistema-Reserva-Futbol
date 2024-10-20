package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Horario;

import java.util.List;
import java.util.Optional;

public interface IHorarioService {
    Horario save(Horario horario);
    Horario update(Long id, Horario horario);
    List<Horario> all();
    Horario changeStatus(Long id, Integer status);
    Optional<Horario> byId(Long id);
}
