package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.repository.CampoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampoAvailabilityService {

    private final CampoRepository campoRepository;

    public List<Campo> getAvailableCampos() {
        // Fetch available fields (custom logic based on your model)
        return campoRepository.findAll().stream()
                .filter(this::isCampoAvailable)
                .collect(Collectors.toList());
    }

    private boolean isCampoAvailable(Campo campo) {
        // Define your availability logic, e.g., campo.getEstado() == '1' for available
        return campo.getEstado() == '1';
    }

    public void updateCampoStatus(Long campoId, boolean enUso) {
        Campo campo = campoRepository.findById(campoId)
                .orElseThrow(() -> new RuntimeException("Campo not found"));
        campo.setEnUso(enUso);
        campoRepository.save(campo);
    }

    public Campo getCampoById(Long campoId) {
        return campoRepository.findById(campoId)
                .orElseThrow(() -> new RuntimeException("Campo not found"));
    }
}
