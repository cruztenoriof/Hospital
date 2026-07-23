package com.francisco.citas.repository;


import com.francisco.citas.entity.Cita;
import com.francisco.citas.enums.EstadoCita;
import com.francisco.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository <Cita, Long> {

    List<Cita> findByEstadoRegistro (EstadoRegistro estadoRegistro);

    Optional <Cita> findByIdAndEstadoRegistro (Long id, EstadoRegistro estadoRegistro);

    boolean existsByIdPacienteAndEstadoCitaInAndEstadoRegistro(
            Long idPaciente,
            List<EstadoCita> estados,
            EstadoRegistro estadoRegistro);

}
