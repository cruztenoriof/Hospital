package com.francisco.medicos.service;


import com.francisco.commons.dto.medico.MedicoRequest;
import com.francisco.commons.dto.medico.MedicoResponse;
import com.francisco.commons.enums.DisponibilidadMedico;
import com.francisco.commons.enums.EspecialidadMedico;
import com.francisco.commons.enums.EstadoRegistro;
import com.francisco.commons.exceptions.RecursoNoEncontradoException;
import com.francisco.medicos.Entity.Medico;
import com.francisco.medicos.mapper.MedicoMapper;
import com.francisco.medicos.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        log.info("Buscando medico sin estado con id {}", id);
        return medicoMapper.entidadAResponse(medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Médico sin estado no encontrado con id: " + id)));
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDiponibilidad) {
    Medico medico = obtenerMedicoActivoPorExcepcion(idMedico);
    log.info("Actualizando la disponibilidad del médico con id: {}", idMedico);
        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico.
                obtenerDisponibilidadporCodigo(idDiponibilidad);
        medico.actualizarDisponibilidad(nuevaDisponibilidad); //
        medicoRepository.save(medico);
    }

    @Override
    public List<MedicoResponse> listar() {
        log.info("Listando todos los médicos activos");
        return medicoRepository.findByEstadoRegistro(EstadoRegistro.Activo)
                .stream()
                .map(medicoMapper::entidadAResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(Long id) {
        return medicoMapper.entidadAResponse(obtenerMedicoActivoPorExcepcion(id));
    }
    @Override
    public MedicoResponse registar(MedicoRequest request) {
        log.info("Registrando nuevo médico {}", request.nombre());
        validarDatosUnicos(request);
        Medico medico = medicoMapper.requestAEntidad(request);
        medico.actualizarEspecialidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad())
        );
        medicoRepository.save(medico);
        log.info("Nuevo médico registrado: {}", medico.getNombre());
        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {
        Medico medico = obtenerMedicoActivoPorExcepcion(id);
        log.info("Actualizando Médico con id: {}", id);
        validarCambiosUnicos(request, id);
        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad())
        );
        log.info("Medico actualizado con éxito: {}", id);
        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void eliminar(Long id) {
        Medico medico = obtenerMedicoActivoPorExcepcion(id);
        log.info("Eliminando Médico con id: {}", id);
        medico.eliminar();
    log.info("Medico con id {} ha sido eliminado", id);
    }

    private Medico obtenerMedicoActivoPorExcepcion(Long id) {
        log.info("Buscando médico activo con id {}", id);
        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.Activo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico activo no encontrado con id: " + id));
    }

    public void validarDatosUnicos(MedicoRequest request) {
        log.info("Validando email único...");
        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.Activo)) {
            throw new IllegalArgumentException("Ya existe un médico activo registrado con el email: " + request.email());
        }
        log.info("Validando telefono único...");
        if (medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.Activo)) {
            throw new IllegalArgumentException("Ya existe un médico activo registrado con el telefono: " +
                    request.telefono());
        }
        log.info("Validando cedúla profesional única...");
        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional().trim(), EstadoRegistro.Activo)) {
            throw new IllegalArgumentException("Ya existe un médico activo con la cedúla profesional: " +
                    request.cedulaProfesional());
        }
    }
    public void validarCambiosUnicos(MedicoRequest request, Long id) {
        log.info("Validando email único...");
        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.Activo)) {
            throw new IllegalArgumentException("Ya existe un médico activo registrado con el email: " + request.email());
        }
        log.info("Validando telefono único...");
        if (medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.Activo)) {
            throw new IllegalArgumentException("Ya existe un médico activo registrado con el telefono: " +
                    request.telefono());
        }
        log.info("Validando cedúla profesional única...");
        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional().trim(), EstadoRegistro.Activo)) {
            throw new IllegalArgumentException("Ya existe un médico activo con la cedúla profesional: " +
                    request.cedulaProfesional());
        }
    }
}