package com.francisco.authorizacion.services;

import com.francisco.authorizacion.dto.UsuarioResponse;
import com.francisco.authorizacion.dto.UsuarioRequest;

import java.util.Set;

public interface UsuarioService {

    Set<UsuarioResponse> listar();

    UsuarioResponse registrar (UsuarioRequest request);

    UsuarioResponse eliminar(String username);
}
