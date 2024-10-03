package com.cruz_sur.api.service.imp;


import com.cruz_sur.api.model.Cliente;
import com.cruz_sur.api.model.Rol;
import com.cruz_sur.api.model.Usuario;
import com.cruz_sur.api.repository.ClienteRepository;
import com.cruz_sur.api.repository.RolRepository;
import com.cruz_sur.api.repository.UsuarioRepository;
import com.cruz_sur.api.service.UsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Usuario save(Usuario usuario) {
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(usuario.getEmail());
        if (existingUser.isPresent()) {
            Usuario userToUpdate = existingUser.get();
            return usuarioRepository.save(userToUpdate);
        } else {
            return usuarioRepository.save(usuario);
        }
    }
    public Usuario createUser(String logeo, String clave, String email, Long rolId, Long clienteId, String usuarioCreacion) {
        Usuario usuario = new Usuario();
        usuario.setLogeo(logeo);
        usuario.setClave(clave);
        usuario.setEmail(email);
        usuario.setRol(rolRepository.findById(rolId).orElseThrow(() -> new RuntimeException("Rol no encontrado")));

        if (clienteId != null) {
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            usuario.setCliente(cliente);
        }

        usuario.setUsuarioCreacion(usuarioCreacion);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setEstado('1');

        return usuarioRepository.save(usuario);
    }

    public Usuario authenticateUser(String email, String clave) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getClave().equals(clave)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return usuario;
    }

    @Override
    public Rol getDefaultUserRole() {
        return rolRepository.findByNomtipo("USER").orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
    }
}
