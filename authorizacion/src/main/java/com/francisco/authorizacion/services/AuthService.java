package com.francisco.authorizacion.services;

import com.francisco.authorizacion.dto.LoginRequest;
import com.francisco.authorizacion.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}
