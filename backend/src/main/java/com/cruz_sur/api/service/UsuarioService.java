package com.cruz_sur.api.service;


import com.cruz_sur.api.model.Rol;
import com.cruz_sur.api.model.Usuario;

import java.util.Optional;

public interface UsuarioService {
    Usuario save(Usuario usuario);
    Rol getDefaultUserRole();
    Usuario findById(Long userId);
    Optional<Usuario> findByEmail(String email); // Método para buscar por email

    Usuario createUser(String logeo, String clave, String email, Long rolId, Long clienteId, Long companiaId, String usuarioCreacion);

    Usuario authenticateUser(String email, String clave);


}
