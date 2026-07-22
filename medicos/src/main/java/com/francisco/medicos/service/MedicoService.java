package com.francisco.medicos.service;


import com.francisco.commons.dto.medico.MedicoRequest;
import com.francisco.commons.dto.medico.MedicoResponse;
import com.francisco.commons.services.CrudService;

public interface MedicoService extends CrudService<MedicoResponse, MedicoRequest> {

    MedicoResponse obtenerMedicoPorIdSinEstado (Long id);

    void actualizarDisponibilidadMedico (Long idMedico, Long idDiponibilidad);

}
