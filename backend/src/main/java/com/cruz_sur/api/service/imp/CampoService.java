package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Campo;
import com.cruz_sur.api.repository.CampoRepository;
import com.cruz_sur.api.service.ICampoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CampoService implements ICampoService {

    private final CampoRepository campoRepository;

    @Override
    public Campo save(Campo campo) {
        return campoRepository.save(campo);
    }

    @Override
    public Campo update(Long id, Campo campo) {
        Campo campoExistente = campoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Campo no encontrado")
        );
        campoExistente.setNombre(campo.getNombre());
        campoExistente.setPrecio(campo.getPrecio());
        campoExistente.setDescripcion(campo.getDescripcion());
        campoExistente.setUsuarioCreacion(campo.getUsuarioCreacion());
        campoExistente.setFechaCreacion(campo.getFechaCreacion());
        campoExistente.setUsuarioModificacion(campo.getUsuarioModificacion());
        campoExistente.setFechaModificacion(campo.getFechaModificacion());

        return campoRepository.save(campoExistente);
    }

    @Override
    public List<Campo> all() {
        return campoRepository.findAll();
    }

    @Override
    public Campo changeStatus(Long id, Integer status) {
        Campo campo = campoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado"));

        campo.setEstado(status == 1 ? '1' : '0');
        return campoRepository.save(campo);
    }

    @Override
    public Optional<Campo> byId(Long id) {
        return campoRepository.findById(id);
    }
}
