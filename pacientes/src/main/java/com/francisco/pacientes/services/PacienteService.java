package com.francisco.pacientes.services;

import com.francisco.commons.dto.pacientes.PacienteRequest;
import com.francisco.commons.dto.pacientes.PacienteResponse;
import com.francisco.commons.services.CrudService;

public interface PacienteService extends CrudService <PacienteResponse, PacienteRequest> {

    PacienteResponse obtenerPacientePorIdSinEstado (Long id);
}
