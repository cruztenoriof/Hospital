    package com.francisco.medicos.mapper;

    import com.francisco.commons.dto.medico.MedicoRequest;
    import com.francisco.commons.dto.medico.MedicoResponse;
    import com.francisco.commons.enums.DisponibilidadMedico;
    import com.francisco.commons.enums.EstadoRegistro;
    import com.francisco.commons.mapper.CommonMapper;
    import com.francisco.medicos.Entity.Medico;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;

    @RequiredArgsConstructor
    @Component
    public class MedicoMapper implements CommonMapper <MedicoRequest, MedicoResponse, Medico> {

        @Override
        public Medico requestAEntidad(MedicoRequest request) {
            if (request == null) return null;
            return Medico.builder()
                    .nombre(request.nombre().trim())
                    .apellidoPaterno(request.apellidoPaterno().trim())
                    .apellidoMaterno(request.apellidoMaterno().trim())
                    .edad(request.edad())
                    .email(request.email().toLowerCase().trim())
                    .telefono(request.telefono().trim())
                    .cedulaProfesional(request.cedulaProfesional().trim())
                    .disponibilidad(DisponibilidadMedico.DISPONIBLE)
                    .estadoRegistro(EstadoRegistro.Activo)
                    .build();
        }

        @Override
        public MedicoResponse entidadAResponse(Medico entidad) {
            if (entidad == null) return null;
            return new MedicoResponse(
                    entidad.getId(),
                    String.join(" ", entidad.getNombre(), entidad.getApellidoPaterno(), entidad.getApellidoMaterno()),
                    entidad.getEdad(),
                    entidad.getEmail(),
                    entidad.getTelefono(),
                    entidad.getCedulaProfesional(),
                    entidad.getEspecialidad().getDescripcion(),
                    entidad.getDisponibilidad().getDescripcion(),
                    entidad.getDisponibilidad().getId()
            );
        }
    }
