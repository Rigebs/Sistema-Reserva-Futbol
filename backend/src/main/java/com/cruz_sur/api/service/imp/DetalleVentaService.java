package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.DetalleVentaDTO;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.repository.CampoRepository;
import com.cruz_sur.api.repository.DetalleVentaRepository;
import com.cruz_sur.api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final CampoRepository campoRepository;
    private final UserRepository userRepository;

    public void createDetalleVenta(DetalleVentaDTO detalleVentaDTO, Reserva reserva) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime now = LocalDateTime.now();
        User usuario = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Campo campo = campoRepository.findById(detalleVentaDTO.getCampoId())
                .orElseThrow(() -> new RuntimeException("Campo not found for id: " + detalleVentaDTO.getCampoId()));

        DetalleVenta detalleVenta = DetalleVenta.builder()
                .venta(reserva)
                .campo(campo)
                .horaInicio(detalleVentaDTO.getHoraInicio())
                .horaFinal(detalleVentaDTO.getHoraFinal())
                .precio(detalleVentaDTO.getPrecio())
                .usuario(usuario)
                .usuarioCreacion(authenticatedUsername)
                .fechaCreacion(now)
                .estado('1')
                .build();

        detalleVentaRepository.save(detalleVenta);
    }
}
