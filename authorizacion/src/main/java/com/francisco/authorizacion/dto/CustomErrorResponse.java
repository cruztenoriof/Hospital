package com.francisco.authorizacion.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) { }

