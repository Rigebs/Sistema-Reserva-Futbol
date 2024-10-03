package com.cruz_sur.api.service;


import com.cruz_sur.api.model.Rol;
import com.cruz_sur.api.model.Usuario;

public interface UsuarioService {
    Usuario save(Usuario usuario);
    Rol getDefaultUserRole();
}
