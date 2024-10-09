package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Rol;
import com.cruz_sur.api.model.Usuario;
import com.cruz_sur.api.repository.RolRepository;
import com.cruz_sur.api.repository.UsuarioRepository;
import com.cruz_sur.api.service.IUsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService implements IUsuarioService, UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Override
    public Usuario save(Usuario usuario) {

            return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario findById(Long userId) {
        return usuarioRepository.findById(userId).orElse(null);
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
}
