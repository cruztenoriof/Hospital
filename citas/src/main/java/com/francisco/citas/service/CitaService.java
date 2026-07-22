package com.francisco.citas.service;

import com.francisco.citas.dto.CitaRequest;
import com.francisco.citas.dto.CitaResponse;
import com.francisco.commons.services.CrudService;

public interface CitaService extends CrudService <CitaResponse, CitaRequest> {

    void actualizarEstadoCita (Long idCita, Long idEstadoCita);
}
