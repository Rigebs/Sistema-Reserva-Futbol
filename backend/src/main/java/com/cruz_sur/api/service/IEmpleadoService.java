package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Empleado;

import java.util.List;
import java.util.Optional;

public interface IEmpleadoService {

    List<Empleado> all();
    Optional<Empleado> byId(Long id);
    Empleado save(Empleado empleado);
    Empleado update(Long id, Empleado empleado);
    Empleado changeStatus(Long id, Integer status);
}
