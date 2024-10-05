package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Empleado;
import com.cruz_sur.api.repository.EmpleadoRepository;
import com.cruz_sur.api.service.IEmpleadoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmpleadoService implements IEmpleadoService {
    private final EmpleadoRepository empleadoRepository;

    @Override
    public List<Empleado> all() {
        return empleadoRepository.findAll();
    }

    @Override
    public Optional<Empleado> byId(Long id) {
        return empleadoRepository.findById(id);
    }

    @Override
    public Empleado update(Long id, Empleado empleado) {
        Empleado existingEmpleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrada"));

        existingEmpleado.setFechaIngreso(empleado.getFechaIngreso());
        existingEmpleado.setFechaSalida(empleado.getFechaSalida());
        existingEmpleado.setSueldo(empleado.getSueldo());
        existingEmpleado.setUsuarioCreacion(empleado.getUsuarioCreacion());
        existingEmpleado.setUsuarioModificacion(empleado.getUsuarioModificacion());
        existingEmpleado.setFechaModificacion(empleado.getFechaModificacion());
        existingEmpleado.setPersona(empleado.getPersona());
        existingEmpleado.setCargo(empleado.getCargo());
        return empleadoRepository.save(existingEmpleado);
    }

    @Override
    public Empleado save(Empleado Empleado) {
        return empleadoRepository.save(Empleado);
    }

    @Override
    public Empleado changeStatus(Long id, Integer status) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrada"));

        empleado.setEstado(status == 1 ? '1' : '0');
        return empleadoRepository.save(empleado);
    }
}
