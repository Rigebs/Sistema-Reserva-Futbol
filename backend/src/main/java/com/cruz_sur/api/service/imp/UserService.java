package com.cruz_sur.api.service.imp;


import com.cruz_sur.api.dto.UserDetailsDTO;
import com.cruz_sur.api.model.*;
import com.cruz_sur.api.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CompaniaRepository companiaRepository;
    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final PersonaRepository personaRepository;
    @Transactional
    public User updateAllFields(Long id, UserDetailsDTO userDetailsDTO) {
        // Buscar el usuario existente
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar datos del usuario principal
        existingUser.setUsername(userDetailsDTO.getUsername());
        existingUser.setEmail(userDetailsDTO.getEmail());
        existingUser.setEnabled(true); // Asegurar que el usuario está habilitado

        // Actualizar la compañía asociada (si existe)
        if (userDetailsDTO.getCompania() != null) {
            Compania compania = companiaRepository.findById(userDetailsDTO.getCompania().getId())
                    .orElseThrow(() -> new RuntimeException("Compañía no encontrada"));
            compania.setNombre(userDetailsDTO.getCompania().getNombre());
            compania.setConcepto(userDetailsDTO.getCompania().getConcepto());
            compania.setCorreo(userDetailsDTO.getCompania().getCorreo());
            compania.setCelular(userDetailsDTO.getCompania().getCelular());
            compania.setHoraInicio(userDetailsDTO.getCompania().getHoraInicio());
            compania.setHoraFin(userDetailsDTO.getCompania().getHoraFin());
            companiaRepository.save(compania);
            existingUser.setSede(compania);
        }

        // Actualizar la empresa asociada (si existe)
        if (userDetailsDTO.getEmpresa() != null) {
            Empresa empresa = empresaRepository.findById(userDetailsDTO.getEmpresa().getId())
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
            empresa.setRuc(userDetailsDTO.getEmpresa().getRuc());
            empresa.setRazonSocial(userDetailsDTO.getEmpresa().getRazonSocial());
            empresa.setTelefono(userDetailsDTO.getEmpresa().getTelefono());
            empresa.setDireccion(userDetailsDTO.getEmpresa().getDireccion());
            empresaRepository.save(empresa);
        }

        // Actualizar cliente y sus relaciones (Persona o Empresa)
        if (userDetailsDTO.getCliente() != null) {
            Cliente cliente = clienteRepository.findById(userDetailsDTO.getCliente().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            // Manejar Persona
            if (userDetailsDTO.getCliente().getPersona() != null) {
                Persona persona;
                if (cliente.getPersona() != null) {
                    // Actualizar la Persona existente
                    persona = personaRepository.findById(cliente.getPersona().getId())
                            .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
                } else {
                    // Crear nueva Persona
                    persona = new Persona();
                }
                persona.setNombre(userDetailsDTO.getCliente().getPersona().getNombre());
                persona.setApePaterno(userDetailsDTO.getCliente().getPersona().getApePaterno());
                persona.setApeMaterno(userDetailsDTO.getCliente().getPersona().getApeMaterno());
                persona.setCorreo(userDetailsDTO.getCliente().getPersona().getCorreo());
                persona.setDni(userDetailsDTO.getCliente().getPersona().getDni());
                persona.setCelular(userDetailsDTO.getCliente().getPersona().getCelular());
                persona.setDireccion(userDetailsDTO.getCliente().getPersona().getDireccion());
                persona.setGenero(userDetailsDTO.getCliente().getPersona().getGenero());
                personaRepository.save(persona);
                cliente.setPersona(persona);
            } else {
                // Si no hay Persona en el DTO, limpiar la relación
                cliente.setPersona(null);
            }

            // Manejar Empresa
            if (userDetailsDTO.getCliente().getEmpresa() != null) {
                Empresa clienteEmpresa;
                if (cliente.getEmpresa() != null) {
                    // Actualizar la Empresa existente
                    clienteEmpresa = empresaRepository.findById(cliente.getEmpresa().getId())
                            .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
                } else {
                    // Crear nueva Empresa
                    clienteEmpresa = new Empresa();
                }
                clienteEmpresa.setRuc(userDetailsDTO.getCliente().getEmpresa().getRuc());
                clienteEmpresa.setRazonSocial(userDetailsDTO.getCliente().getEmpresa().getRazonSocial());
                clienteEmpresa.setTelefono(userDetailsDTO.getCliente().getEmpresa().getTelefono());
                clienteEmpresa.setDireccion(userDetailsDTO.getCliente().getEmpresa().getDireccion());
                empresaRepository.save(clienteEmpresa);
                cliente.setEmpresa(clienteEmpresa);
            } else {
                // Si no hay Empresa en el DTO, limpiar la relación
                cliente.setEmpresa(null);
            }

            cliente.setUsuarioModificacion(SecurityContextHolder.getContext().getAuthentication().getName());
            cliente.setFechaModificacion(LocalDateTime.now());
            clienteRepository.save(cliente);
            existingUser.setCliente(cliente);
        }

        // Guardar los cambios del usuario
        existingUser.setUsuarioModificacion(SecurityContextHolder.getContext().getAuthentication().getName());
        existingUser.setFechaModificacion(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public UserDetailsDTO byId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setUserId(user.getId());
        userDetailsDTO.setUsername(user.getUsername());
        userDetailsDTO.setEmail(user.getEmail());
        userDetailsDTO.setRoles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.joining(", ")));

        // Si el usuario tiene asociada una compañía, agregar la información
        if (user.getSede() != null) {
            userDetailsDTO.setCompania(user.getSede());
            userDetailsDTO.setEmpresa(user.getSede().getEmpresa());
        }

        // Si el usuario es un cliente, agregar información del cliente
        if (user.getCliente() != null) {
            userDetailsDTO.setCliente(user.getCliente());
            userDetailsDTO.setEmpresa(user.getCliente().getEmpresa());
        }

        return userDetailsDTO;
    }


}