package com.francisco.medicos.Controller;

import com.francisco.commons.controller.CommonController;
import com.francisco.commons.dto.medico.MedicoRequest;
import com.francisco.commons.dto.medico.MedicoResponse;
import com.francisco.medicos.service.MedicoService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Validated
public class MedicoController extends CommonController<MedicoRequest, MedicoResponse, MedicoService> {
    public MedicoController(MedicoService service) {

        super(service);
    }
    @GetMapping("/id-medico/{id}")
    public ResponseEntity<MedicoResponse> obtenerMedicoPorIdSinEstado(
            @PathVariable @Positive (message = "El ID debe ser postivo") Long id){
        return ResponseEntity.ok(service.obtenerMedicoPorIdSinEstado(id));
    }
    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    public ResponseEntity<Void> actualizarDisponibilidadMedico(
            @PathVariable @Positive (message = "El idMedico debe ser positivo") Long idMedico,
            @PathVariable @Positive (message = "El idDisponibilidad debe ser positivo") Long idDisponibilidad){
        service.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
        return ResponseEntity.noContent().build();
    }
}
