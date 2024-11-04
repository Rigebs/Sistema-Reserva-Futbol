package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.model.DetalleVenta;
import com.cruz_sur.api.service.imp.CampoAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.sql.Time;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class AvailabilityController {
    private final SimpMessagingTemplate messagingTemplate;
    private final CampoAvailabilityService campoAvailabilityService;

    public void notifyCampoStatusChange(Campo campo) {
        messagingTemplate.convertAndSend("/topic/campoStatus", campo);
    }

    public boolean checkCampoAvailability(Long campoId, LocalDate fecha, Time horaInicio, Time horaFin) {
        boolean available = campoAvailabilityService.isCampoAvailable(campoId, fecha, horaInicio, horaFin);
        messagingTemplate.convertAndSend("/topic/campoAvailability", available);
        return available;
    }
}
