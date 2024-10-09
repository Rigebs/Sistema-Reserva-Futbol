package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Provincia;
import com.cruz_sur.api.service.IProvinciaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/provincia")
@AllArgsConstructor
public class ProvinciaController {
    private final IProvinciaService iProvinciaService;

    @GetMapping
    public List<Provincia> all(){
        return iProvinciaService.all();
    }

    @GetMapping("/{id}")
    public Optional<Provincia> byId(@PathVariable Long id){
        return iProvinciaService.byId(id);
    }

    @GetMapping("/departamento/{departamentoId}")
    public List<Provincia> findDepartamentoById(@PathVariable Long departamentoId){
        return iProvinciaService.findDepartamentoById(departamentoId);
    }
}
