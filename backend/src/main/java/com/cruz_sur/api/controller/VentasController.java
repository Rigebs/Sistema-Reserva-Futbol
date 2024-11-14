package com.cruz_sur.api.controller;

import com.cruz_sur.api.dto.ContarReservasDTO;
import com.cruz_sur.api.dto.VentasMensualesDTO;
import com.cruz_sur.api.service.IVentasService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@AllArgsConstructor
public class VentasController {

    private final IVentasService ventasService;

    @GetMapping
    public List<VentasMensualesDTO> all() {
        return ventasService.all();
    }

    @GetMapping("/reservas/dia")
    public ContarReservasDTO allDay(@RequestParam String fecha) {
        return ventasService.allDay(fecha);
    }
}
