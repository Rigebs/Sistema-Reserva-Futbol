package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.service.imp.CampoAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/availability")
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

    // New endpoint for getting available hours
    @GetMapping("/available-hours/{campoId}")
    public ResponseEntity<List<Time>> getAvailableHours(@PathVariable Long campoId, @RequestParam LocalDate fecha) {
        List<Time> allHours = getAllHours(); // Replace with your method to get all possible hours
        List<Time> availableHours = campoAvailabilityService.getAvailableHours(campoId, fecha, allHours);
        return ResponseEntity.ok(availableHours);
    }

    private List<Time> getAllHours() {
        // Implement logic to generate the list of all possible hours (e.g., from 00:00 to 23:59)
        List<Time> allHours = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            allHours.add(Time.valueOf(LocalTime.of(hour, 0)));
        }
        return allHours;
    }
}
