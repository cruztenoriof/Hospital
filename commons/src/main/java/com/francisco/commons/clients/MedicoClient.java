package com.francisco.commons.clients;


import com.francisco.commons.dto.medico.MedicoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "medicos")
public interface MedicoClient {

    @GetMapping("/{id}")
    MedicoResponse obtenerMedicoActivoPorId(@PathVariable Long id);

    @GetMapping("/id-medico/{id}")
    MedicoResponse obtenerMedicoSinEstadoPorId(@PathVariable Long id);

    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    void actualizarDisponibilidad(@PathVariable("idMedico") Long idMedico,
                                  @PathVariable("idDisponibilidad") Long idDisponibilidad);
}
