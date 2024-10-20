package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Rol;
import com.cruz_sur.api.model.Usuario;
import com.cruz_sur.api.model.Cliente; // Asegúrate de importar Cliente
import com.cruz_sur.api.model.Sede; // Asegúrate de importar Sede
import com.cruz_sur.api.repository.ClienteRepository;
import com.cruz_sur.api.repository.RolRepository;
import com.cruz_sur.api.repository.SedeRepository;
import com.cruz_sur.api.repository.UsuarioRepository;
import com.cruz_sur.api.service.IUsuarioService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class UsuarioService implements IUsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ClienteRepository clienteRepository;
    private final SedeRepository sedeRepository;

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long userId) {
        return usuarioRepository.findById(userId);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Rol getDefaultUserRole() {
        return rolRepository.findByNomtipo("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + email + " no existe"));
        return new UserDetailsimp(user);
    }

    public Cliente getClienteById(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public Sede getSedeById(Long sedeId) {
        return sedeRepository.findById(sedeId)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
    }
}
