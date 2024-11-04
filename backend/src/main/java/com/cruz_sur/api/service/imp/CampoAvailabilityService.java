package com.cruz_sur.api.service.imp;


import com.cruz_sur.api.repository.CampoRepository;
import com.cruz_sur.api.repository.DetalleVentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class CampoAvailabilityService {

    private final CampoRepository campoRepository;
    private final DetalleVentaRepository detalleVentaRepository;


}

