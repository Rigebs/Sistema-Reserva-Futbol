package com.cruz_sur.api.service;


import com.cruz_sur.api.model.Rol;
import com.cruz_sur.api.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

public interface IUsuarioService {
    Usuario save(Usuario usuario);
    Rol getDefaultUserRole();
    Usuario findById(Long userId);
    Optional<Usuario> findByEmail(String email);

}
