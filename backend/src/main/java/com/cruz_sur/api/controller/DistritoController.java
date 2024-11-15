package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Distrito;
import com.cruz_sur.api.service.IDistritoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/distrito")
@AllArgsConstructor
public class DistritoController {

    private final IDistritoService iDistritoService;

    @GetMapping
    public List<Distrito> all(){
        return iDistritoService.all();
    }

    @GetMapping("/{id}")
    public Optional<Distrito> byId(@PathVariable Long id){
        return iDistritoService.byId(id);
    }

    @GetMapping("/provincia/{provinciaId}")
    public List<Distrito> findByProvinciaId(@PathVariable Long provinciaId){
        return iDistritoService.findByProvinciaId(provinciaId);
    }
}
