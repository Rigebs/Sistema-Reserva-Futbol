package com.cruz_sur.api.service;


import com.cruz_sur.api.model.Rol;
import com.cruz_sur.api.model.Usuario;

import java.util.Optional;

public interface IUsuarioService {
    Usuario save(Usuario usuario);
    Rol getDefaultUserRole();
    Usuario findById(Long userId);
    Optional<Usuario> findByEmail(String email);

}
