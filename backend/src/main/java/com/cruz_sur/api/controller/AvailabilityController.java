package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.service.imp.CampoAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
@RequiredArgsConstructor
public class AvailabilityController {
    private final SimpMessagingTemplate messagingTemplate;
    private final CampoAvailabilityService campoAvailabilityService;

    public void notifyCampoStatusChange(Campo campo) {
        messagingTemplate.convertAndSend("/topic/campoStatus", campo);
    }

    @Scheduled(fixedRate = 5000)
    public void checkAndBroadcastAvailability() {
        var availableCampos = campoAvailabilityService.getAvailableCampos();
        messagingTemplate.convertAndSend("/topic/availableCampos", availableCampos);
    }

    @PutMapping("/api/v1/campo/{id}/status/{enUso}")
    public ResponseEntity<String> changeCampoStatus(@PathVariable Long id, @PathVariable boolean enUso) {
        campoAvailabilityService.updateCampoStatus(id, enUso);
        Campo campo = campoAvailabilityService.getCampoById(id);
        notifyCampoStatusChange(campo);
        return new ResponseEntity<>("Campo status updated", HttpStatus.OK);
    }
}
