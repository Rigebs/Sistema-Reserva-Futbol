package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Empleado;
import com.cruz_sur.api.service.IEmpleadoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empleados")
@AllArgsConstructor
public class EmpleadoController {

    private final IEmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        List<Empleado> empleados = empleadoService.all();
        return new ResponseEntity<>(empleados, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEmpleadoById(@PathVariable Long id) {
        Empleado empleado = empleadoService.byId(id).orElse(null);
        return empleado != null
                ? new ResponseEntity<>(empleado, HttpStatus.OK)
                : new ResponseEntity<>("Empleado no encontrado", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createEmpleado(@RequestBody Empleado empleado) {
        empleadoService.save(empleado);
        return new ResponseEntity<>("Empleado creado con éxito", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEmpleado(@PathVariable Long id, @RequestBody Empleado empleado) {
        empleadoService.update(id, empleado);
        return new ResponseEntity<>("Empleado actualizado con éxito", HttpStatus.OK);
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        empleadoService.changeStatus(id, status);
        String statusMessage = status == 1 ? "activado" : "desactivado";
        return new ResponseEntity<>("Empleado " + statusMessage + " con éxito", HttpStatus.OK);
    }
}
