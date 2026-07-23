package com.francisco.citas.entity;


import com.francisco.citas.enums.EstadoCita;
import com.francisco.commons.enums.EstadoRegistro;
import com.francisco.commons.utils.StringCustomUtils;
import com.francisco.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CITAS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CITA")
    private Long id;

    @Column(name = "ID_PACIENTE", nullable = false)
    private Long idPaciente;

    @Column(name = "ID_MEDICO", nullable = false)
    private Long idMedico;

    @Column(name = "FECHA_CITA", nullable = false)
    private LocalDateTime fechaCita;

    @Column(name = "SINTOMAS", nullable = false, length = 500)
    private String sintomas;

    @Column(name = "ESTADO_CITA", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoCita estadoCita;

    @Column(name = "ESTADO_REGISTRO", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoRegistro estadoRegistro;

    public static Cita crear (Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas){

        validarDatos (idPaciente, idMedico, fechaCita, sintomas);

        return Cita.builder()
                .idPaciente(idPaciente)
                .idMedico (idMedico)
                .fechaCita(fechaCita)
                .sintomas(sintomas.trim())
                .estadoCita(EstadoCita.PENDIENTE)
                .estadoRegistro(EstadoRegistro.Activo)
                .build();
    }

    public void actualizar (Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas){
        validarActualizacionPermitida();
        validarDatos(idPaciente, idMedico, fechaCita, sintomas);

        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fechaCita = fechaCita;
        this.sintomas = sintomas.trim();
    }

    public  void actualizarEstadoCita (EstadoCita nuevoEstado){
        validarActualizacionPermitida();

        if (nuevoEstado == null)
            throw new IllegalArgumentException("El nuevo estado de la cita es requerido");

        if(!this.estadoCita.puedeCambiarA(nuevoEstado))
            throw new IllegalStateException("La cita con estado "
                    +this.estadoCita+ " solo puede cambiar a: "
                    +this.estadoCita.puedeCambiar());

        this.estadoCita = nuevoEstado;
    }

    private static void validarId(Long id, String campo){
        ValoresNumericosUtils.validarLongPositivo(id,
                "El id del "+ campo + " es requerido y debe ser positivo");
    }
    private static void validarFechaCita (LocalDateTime fechaCita){
        if(fechaCita  == null || !fechaCita.isAfter(LocalDateTime.now()))
            throw new IllegalStateException("La fecha de la cita es requerida y debe ser futura");
    }
    public void  eliminar () {
        validarNoEliminado();
        if (estadoCita.isEliminable())
            this.estadoRegistro = EstadoRegistro.Eliminado;
    }

    private void  validarNoEliminado (){
        if (this.estadoRegistro == EstadoRegistro.Eliminado)
            throw new IllegalArgumentException("La cita ya esta eliminada");
    }

    private void  validarEliminacionPermitida (){
        validarNoEliminado();
        if (!this.estadoCita.isEliminable()) {
            throw new IllegalArgumentException("La cita con estado: " + this.estadoCita + " no se puede eliminar.");
        }
    }

    private void  validarActualizacionPermitida (){
        validarNoEliminado();
        if (!this.estadoCita.isActualizable())
            throw new IllegalArgumentException("La cita con estado: " + this.estadoCita + " no se puede actualizar");
    }
    public static void validarDatos(
            Long idPaciente, Long idMedico,
            LocalDateTime fechaCita, String sintomas){

        validarId(idPaciente, "paciente");

        validarId(idMedico, "médico");

        validarFechaCita(fechaCita);

        StringCustomUtils.validarTamanio(sintomas, 20, 500,
                "Los síntomas son requeridos");
    }
}
