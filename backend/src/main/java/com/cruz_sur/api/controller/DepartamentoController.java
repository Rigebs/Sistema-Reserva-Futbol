package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Departamento;
import com.cruz_sur.api.service.IDepartamentoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/departamento")
public class DepartamentoController {
    private final IDepartamentoService iDepartamentoService;

    @GetMapping
    public List<Departamento> all(){
        return iDepartamentoService.all();
    }

    @GetMapping("/{id}")
    public Optional<Departamento> byId(@PathVariable Long id){
        return iDepartamentoService.byId(id);
    }

}
