package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Persona;
import com.cruz_sur.api.model.Cliente;
import com.cruz_sur.api.repository.PersonaRepository;
import com.cruz_sur.api.repository.ClienteRepository;
import com.cruz_sur.api.service.IPersonaService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonaService implements IPersonaService {
    private final PersonaRepository personaRepository;
    private final ClienteRepository clienteRepository;

    @Override
    public List<Persona> all() {
        return personaRepository.findAll();
    }

    @Override
    public Optional<Persona> byId(Long id) {
        return personaRepository.findById(id);
    }

    @Transactional
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
        existingPersona.setDistrito(persona.getDistrito());

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        existingPersona.setUsuarioModificacion(authenticatedUsername);
        existingPersona.setFechaModificacion(LocalDateTime.now());

        personaRepository.save(existingPersona);

        Cliente cliente = clienteRepository.findByPersona(existingPersona)
                .orElseGet(() -> {
                    Cliente newCliente = new Cliente();
                    newCliente.setPersona(existingPersona);
                    newCliente.setEstado('1'); // Active by default
                    newCliente.setUsuarioCreacion(authenticatedUsername);
                    newCliente.setFechaCreacion(LocalDateTime.now());
                    return newCliente;
                });

        cliente.setEstado('1');
        cliente.setUsuarioModificacion(authenticatedUsername);
        cliente.setFechaModificacion(LocalDateTime.now());

        // Save the Cliente
        clienteRepository.save(cliente);

        return existingPersona;
    }

    @Transactional
    @Override
    public Persona save(Persona persona) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        persona.setUsuarioCreacion(authenticatedUsername);
        persona.setFechaCreacion(LocalDateTime.now());

        persona.setEstado('1');

        Persona newPersona = personaRepository.save(persona);

        Cliente cliente = new Cliente();
        cliente.setPersona(newPersona);
        cliente.setEstado('1');
        cliente.setUsuarioCreacion(authenticatedUsername);
        cliente.setFechaCreacion(LocalDateTime.now());
        clienteRepository.save(cliente);
        return newPersona;
    }


    @Override
    public Persona changeStatus(Long id, Integer status) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        persona.setEstado(status == 1 ? '1' : '0');
        return personaRepository.save(persona);
    }
}
