package com.francisco.pacientes.mappers;

import com.francisco.commons.dto.pacientes.PacienteRequest;
import com.francisco.commons.dto.pacientes.PacienteResponse;
import com.francisco.commons.enums.EstadoRegistro;
import com.francisco.commons.mapper.CommonMapper;
import com.francisco.pacientes.entity.Paciente;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper implements CommonMapper <PacienteRequest, PacienteResponse, Paciente> {

    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        if (request == null) return null;
        return Paciente.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .email(request.email().toLowerCase().trim())
                .edad(request.edad())
                .estatura(request.estatura())
                .peso(request.peso())
                .telefono(request.telefono().trim())
                .direccion(request.direccion().trim())
                .estadoRegistro(EstadoRegistro.Activo)
                .build();
    }

    @Override
    public PacienteResponse entidadAResponse(Paciente entidad) {
        if (entidad == null) return null;
        return new PacienteResponse(
                entidad.getId(),
                String.join(" ",
                        entidad.getNombre(),
                        entidad.getApellidoPaterno(),
                        entidad.getApellidoMaterno()),
                entidad.getEdad(),
                entidad.getPeso(),
                entidad.getEstatura(),
                entidad.getImc(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getDireccion(),
                entidad.getNumExpediente()
        );
    }
}
