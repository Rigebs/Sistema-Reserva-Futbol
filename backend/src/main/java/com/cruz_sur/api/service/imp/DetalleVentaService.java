package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.DetalleVentaDTO;
import com.cruz_sur.api.model.DetalleVenta;
import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.model.Reserva;
import com.cruz_sur.api.repository.CampoRepository;
import com.cruz_sur.api.repository.DetalleVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DetalleVentaService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private CampoRepository campoRepository;

    public void createDetalleVenta(DetalleVentaDTO detalleVentaDTO, Reserva reserva) {
        Campo campo = campoRepository.findById(detalleVentaDTO.getCampoId())
                .orElseThrow(() -> new RuntimeException("Campo not found"));

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();

        DetalleVenta detalleVenta = DetalleVenta.builder()
                .campo(campo)
                .venta(reserva)
                .usuario(reserva.getUsuario())
                .estado('1')
                .usuarioCreacion(authenticatedUsername) // Set the user who created the record
                .fechaCreacion(now) // Set creation date
                .fechaModificacion(now) // Set modification date
                .build();

        detalleVentaRepository.save(detalleVenta);
    }
}
