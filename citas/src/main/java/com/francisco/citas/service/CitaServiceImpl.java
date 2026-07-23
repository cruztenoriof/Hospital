package com.francisco.citas.service;

import com.francisco.citas.dto.CitaRequest;
import com.francisco.citas.dto.CitaResponse;
import com.francisco.citas.entity.Cita;
import com.francisco.citas.enums.EstadoCita;
import com.francisco.citas.mapper.CitasMapper;
import com.francisco.citas.repository.CitaRepository;
import com.francisco.commons.clients.MedicoClient;
import com.francisco.commons.clients.PacienteClient;
import com.francisco.commons.dto.medico.MedicoResponse;
import com.francisco.commons.dto.pacientes.PacienteResponse;
import com.francisco.commons.enums.DisponibilidadMedico;
import com.francisco.commons.enums.EstadoRegistro;
import com.francisco.commons.exceptions.RecursoNoEncontradoException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;

    private final CitasMapper citasMapper;

    private final MedicoClient medicoClient;

    private final PacienteClient pacienteClient;

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
        Cita cita = obtenerCitaOException(idCita);

        log.info("Actualizando estado de la cita con id: {}", idCita);

        EstadoCita nuevoEstado = EstadoCita.obtenerCitaporCodigo(idEstadoCita);
        cita.actualizarEstadoCita(nuevoEstado);

        if (nuevoEstado == EstadoCita.CANCELADA || nuevoEstado == EstadoCita.FINALIZADA) {
            log.info("Liberando al médico con id: {} debido a que la cita pasó a estado {}",
                    cita.getIdMedico(), nuevoEstado);
            medicoClient.actualizarDisponibilidad(cita.getIdMedico(),
                    DisponibilidadMedico.DISPONIBLE.getId().longValue());
        } else if (nuevoEstado == EstadoCita.CONFIRMADA || nuevoEstado == EstadoCita.EN_CURSO) {
            log.info("Actualizando al médico con id: {} a no disponible por estado de cita {}",
                    cita.getIdMedico(), nuevoEstado);
            medicoClient.actualizarDisponibilidad(cita.getIdMedico(),
                    DisponibilidadMedico.NO_DISPONIBLE.getId().longValue());
        }
        log.info("Estado de la cita {} actualizado correctamente", cita.getId());
    }
    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {
        log.info("Listando todas las citas activas");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.Activo).stream().
                map(cita -> citasMapper.entidadAResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico()))).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {

        Cita cita = obtenerCitaOException(id);

        return citasMapper.entidadAResponse(cita, obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registar(CitaRequest request) {
        log.info("Registrando nueva cita: {}", request);

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
        validarCitaActivaPaciente(request.idPaciente());

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        validarMedicoDisponible(medico);

        validarFechaPresenteOFuturo(request.fechaCita());

        Cita cita = citasMapper.requestAEntidad(request);

        citaRepository.save(cita);

        log.info("Actualizando disponibilidad del médico con id: {} a NO_DISPONIBLE", cita.getIdMedico());
        medicoClient.actualizarDisponibilidad(cita.getIdMedico(), DisponibilidadMedico.NO_DISPONIBLE.getId().longValue());

        log.info("Cita registrada exitósamente");
        return citasMapper.entidadAResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {

        Cita cita = obtenerCitaOException(id);
        log.info("Validando si la cita con id {} se puede actualizar según su estado", id);
        if (cita.getEstadoCita() != EstadoCita.PENDIENTE && cita.getEstadoCita() != EstadoCita.CONFIRMADA) {
            throw new IllegalArgumentException("La cita solo puede actualizarse si se encuentra en estado PENDIENTE o CONFIRMADA.");
        }
        log.info("Actualizando cita con id: {}", id);
        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );
        log.info("Cita actualizada con id: {}", id);
        return citasMapper.entidadAResponse(cita, obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCitaOException(id);

        log.info("Validando si la cita con id {} se puede eliminar según su estado", id);

        if (cita.getEstadoCita() != EstadoCita.CANCELADA &&
                cita.getEstadoCita() != EstadoCita.FINALIZADA &&
                cita.getEstadoCita() != EstadoCita.PENDIENTE) {
            throw new IllegalArgumentException("La cita solo puede ser eliminada si se encuentra en estado: " +
                    "PENDIENTE, FINALIZADA, CANCELADA.");
        }

        log.info("Eliminando cita con id: {}", id);

        cita.eliminar();

        log.info("Cita con id {} ha sido marcada como eliminada", id);

        log.info("Liberando al médico con id: {} debido a la eliminación de la cita", cita.getIdMedico());
        medicoClient.actualizarDisponibilidad(cita.getIdMedico(), DisponibilidadMedico.DISPONIBLE.getId().longValue());

        log.info("Cita con id {} ha sido dada de baja correctamente", id);
    }
    private Cita obtenerCitaOException(Long id) {
        log.info("Buscando cita con id: {}", id);

        return citaRepository.findById(id).orElseThrow(()
                -> new RecursoNoEncontradoException("Cita no encontrada con id " + id));
    }

    private MedicoResponse obtenerMedicoActivo(Long id) {
        log.info("Buscando medico activo con id {} en el servicio remoto... ", id);
        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id) {
        log.info("Buscando medico sin estado con id {} en el servicio remoto... ", id);
        return medicoClient.obtenerMedicoSinEstadoPorId(id);
    }

    private PacienteResponse obtenerPacienteActivo(Long id) {
        log.info("Buscando paciente activo con id {} en el servicio remoto... ", id);
        return pacienteClient.obtenerPacienteActivoPorId(id);
    }

    private PacienteResponse obtenerPacienteSinEstado(Long id) {
        log.info("Buscando paciente sin estado con id {} en el servicio remoto... ", id);
        return pacienteClient.obtenerPacienteSinEstadoPorId(id);
    }

    private void validarCitaActivaPaciente(Long idPaciente) {
        log.info("Verificando si el paciente con id {} tiene citas activas simultáneas", idPaciente);
        if (citaRepository.existsByIdPacienteAndEstadoCitaIn(idPaciente,
                List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO))) {
            throw new IllegalArgumentException("El paciente ya cuenta con una cita activa " +
                    "(Pendiente, Confirmada o En Curso).");
        }
    }

    private void validarMedicoDisponible(MedicoResponse medico) {
        log.info("Validando disponibilidad del médico con estatus de turno: {}", medico.disponibilidad());

        if (!"DISPONIBLE".equalsIgnoreCase(medico.disponibilidad())) {
            throw new IllegalArgumentException("El médico seleccionado no se encuentra disponible.");
        }
    }

    private void validarFechaPresenteOFuturo(LocalDateTime fechaCita) {
        log.info("Validando vigencia de la fecha: {}", fechaCita);

        if (fechaCita == null) {
            throw new IllegalArgumentException("La fecha de la cita es requerida.");
        }
        if (fechaCita.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora de la cita debe ser en el presente o en el futuro.");
        }
    }
}