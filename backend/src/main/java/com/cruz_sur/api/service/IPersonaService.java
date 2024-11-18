package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Persona;
import com.cruz_sur.api.responses.PersonaClienteResponse;

import java.util.List;
import java.util.Optional;

public interface IPersonaService {
    List<Persona> all();
    Optional<Persona> byId(Long id);
    Persona update(Long id, Persona persona);
    PersonaClienteResponse save(Persona persona);
    Persona changeStatus(Long id, Integer status);

    Optional<Persona> byDni(String dni);
}
