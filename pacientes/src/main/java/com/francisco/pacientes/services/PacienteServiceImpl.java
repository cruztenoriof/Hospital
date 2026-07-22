package com.francisco.pacientes.services;

import com.francisco.commons.dto.pacientes.PacienteRequest;
import com.francisco.commons.dto.pacientes.PacienteResponse;
import com.francisco.commons.enums.EspecialidadMedico;
import com.francisco.commons.enums.EstadoRegistro;
import com.francisco.commons.exceptions.RecursoNoEncontradoException;
import com.francisco.pacientes.entity.Paciente;
import com.francisco.pacientes.mappers.PacienteMapper;
import com.francisco.pacientes.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Transactional
@Service
@RequiredArgsConstructor

public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
        log.info("Buscando pacienete sin estado con id {}", id);
        return pacienteRepository.findById(id)
                .map(pacienteMapper::entidadAResponse)
                .orElseThrow(()-> new IllegalArgumentException("Paciente sin estado no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        log.info("Listando todos los pacientes activos");
        return pacienteRepository.findByEstadoRegistro(EstadoRegistro.Activo).
                stream()
                .map(pacienteMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {
        log.info("Buscando paciente activo con ID: {}", id);
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con el ID: " + id));
        if (paciente.getEstadoRegistro() == EstadoRegistro.Eliminado) {
            throw new IllegalArgumentException("El paciente con el ID " + id + " está eliminado");
        }
        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse registar(PacienteRequest request) {
        log.info("Registrando nuevo paciente {}", request.nombre());
        validarDatosUnicos(request);

        Paciente paciente = pacienteMapper.requestAEntidad(request);
       paciente.asignarImc();
       paciente.asignarNumExpediente();

       Paciente pacienteGuardado = pacienteRepository.save(paciente);
       return pacienteMapper.entidadAResponse(pacienteGuardado);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        log.info("Actualizando paciente con id: {}", id);
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Paciente no encontrado con el ID " + id));
        if (paciente.getEstadoRegistro() == EstadoRegistro.Eliminado) {
            throw new IllegalArgumentException("No se puede actualizar el paciente con ID " + id + " porque se encuentra eliminado.");
        }
        validarCambiosUnicos(request, id);
        paciente.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.email(),
                request.telefono(),
                request.direccion()
        );
        paciente.asignarImc();
        Paciente pacienteActualizado = pacienteRepository.save(paciente);
        log.info("Paciente actualizado con éxito: {}", id);
        return pacienteMapper.entidadAResponse(pacienteActualizado);
    }
    private Paciente obtenerPacienteActivoPorExcepcion(Long id) {
        log.info("Buscando paciente activo con id {}", id);
        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.Activo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente activo no encontrado con id: " + id));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando paciente con id: {}", id);
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con el ID: " + id));
        paciente.eliminar();
        pacienteRepository.save(paciente);
        log.info("Paciente con id {} ha sido eliminado", id);
    }
    private void validarDatosUnicos(PacienteRequest request) {
        log.info("Validando email único...");
        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.Activo)) {
            throw new IllegalArgumentException("Ya existe un paciente activo registrado con el email " + request.email());
        }
        log.info("Validando telefono único...");
        if (pacienteRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.Activo)) {
            throw new IllegalArgumentException("Ya existe un paciente activo registrado con el telefono " +
                    request.telefono());
        }
    }
    private void validarCambiosUnicos(PacienteRequest request, Long id) {
        log.info("Validando email único...");
        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.Activo)) {
            throw new IllegalArgumentException("Ya existe un paciente activo registrado con el email " + request.email());
        }
        if (pacienteRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.Activo)) {
            throw new IllegalArgumentException("Ya existe un paciente activo registrado con el telefono " +
                    request.telefono());
        }
        }
    }