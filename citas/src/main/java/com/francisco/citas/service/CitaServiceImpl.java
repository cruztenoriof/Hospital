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
import com.francisco.commons.enums.EstadoRegistro;
import com.francisco.commons.exceptions.RecursoNoEncontradoException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Circle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        cita.actualizarEstadoCita(EstadoCita.obtenerCitaporCodigo(idEstadoCita));

        log.info("Estado de la cita {} actualizado correctamente", cita.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {
        log.info("Listando todas las citas activas");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.Activo).stream ().
                map(cita -> citasMapper.entidadAResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico()))).toList();
    }

    @Override
    @Transactional (readOnly = true)
    public CitaResponse obtenerPorId(Long id) {

        Cita cita= obtenerCitaOException(id);

        return citasMapper.entidadAResponse(cita,obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registar(CitaRequest request) {
        log.info("Registrando nueva cita: {}", request);

        Cita cita = citasMapper.requestAEntidad(request);

        citaRepository.save(cita);

        log.info("Cita registrada exitósamente");
        return citasMapper.entidadAResponse(cita,obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {

        Cita cita = citasMapper.requestAEntidad(request);
        log.info("Actualizando cita con id: {}", id);
        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );
        log.info("Cita actualizada con id: {}", id);
        return citasMapper.entidadAResponse(cita,obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCitaOException(id);

        log.info("Eliminando cita con id: {}",id);

        cita.eliminar();

        log.info("Cita con id {} ha sido marcada como eliminada", id);
    }

    private Cita obtenerCitaOException(Long id){
        log.info("Buscando cita con id: {}", id);

        return citaRepository.findById(id).orElseThrow(()
                -> new RecursoNoEncontradoException("Cita no encontrada con id " + id));
    }
    private MedicoResponse obtenerMedicoActivo (Long id){
        log.info("Buscando medico activo con id {} en el servicio remoto... ", id);
        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado (Long id){
        log.info("Buscando medico sin estado con id {} en el servicio remoto... ", id);
        return medicoClient.obtenerMedicoSinEstadoPorId(id);
    }
    private PacienteResponse obtenerPacienteActivo (Long id){
        log.info("Buscando paciente activo con id {} en el servicio remoto... ", id);
        return pacienteClient.obtenerPacienteActivoPorId(id);
    }

    private PacienteResponse obtenerPacienteSinEstado (Long id){
        log.info("Buscando paciente sin estado con id {} en el servicio remoto... ", id);
        return pacienteClient.obtenerPacienteSinEstadoPorId(id);
    }
}
