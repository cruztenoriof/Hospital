package com.francisco.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.francisco.commons.dto.medico.DatosMedico;
import com.francisco.commons.dto.pacientes.DatosPaciente;

import java.time.LocalDateTime;

public record CitaResponse(
        Long id,
        DatosPaciente paciente,
        DatosMedico medico,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,
        String sintomas,
        String estadoCita
) {}
