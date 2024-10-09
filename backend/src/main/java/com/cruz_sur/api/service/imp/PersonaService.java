package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Persona;
import com.cruz_sur.api.repository.PersonaRepository;
import com.cruz_sur.api.service.IPersonaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonaService implements IPersonaService {
    private final PersonaRepository personaRepository;

    @Override
    public List<Persona> all() {
        return personaRepository.findAll();
    }

    @Override
    public Optional<Persona> byId(Long id) {
        return personaRepository.findById(id);
    }

    @Override
    public Persona update(Long id, Persona persona) {
        Persona existingPersona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        existingPersona.setNombre(persona.getNombre());
        existingPersona.setApePaterno(persona.getApePaterno());
        existingPersona.setApeMaterno(persona.getApeMaterno());
        existingPersona.setDni(persona.getDni());
        existingPersona.setCelular(persona.getCelular());
        existingPersona.setCorreo(persona.getCorreo());
        existingPersona.setFechaNac(persona.getFechaNac());
        existingPersona.setGenero(persona.getGenero());
        existingPersona.setDireccion(persona.getDireccion());
        existingPersona.setUsuarioModificacion(persona.getUsuarioModificacion());
        existingPersona.setFechaModificacion(persona.getFechaModificacion());
        existingPersona.setDistrito(persona.getDistrito());

        return personaRepository.save(existingPersona);
    }

    @Override
    public Persona save(Persona persona) {
        return personaRepository.save(persona);
    }

    @Override
    public Persona changeStatus(Long id, Integer status) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        persona.setEstado(status == 1 ? '1' : '0');
        return personaRepository.save(persona);
    }
}
