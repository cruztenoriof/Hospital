package com.francisco.medicos.Entity;

import com.francisco.commons.enums.DisponibilidadMedico;
import com.francisco.commons.enums.EspecialidadMedico;
import com.francisco.commons.enums.EstadoRegistro;
import com.francisco.commons.utils.StringCustomUtils;
import com.francisco.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@Table(name = "MEDICOS")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICO")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private  String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column (name = "EMAIL", nullable = false,unique = false, length = 100)
    private String email;

    @Column(name = "TELEFONO", nullable = true, length = 10)
    private String telefono;

    @Column (name = "CEDULA_PROFESIONAL", nullable = false, unique = true, length = 12)
    private String cedulaProfesional;

    @Enumerated(EnumType.STRING)
    @Column (name = "ESPECIALIDAD", nullable = false, length = 30)
    private EspecialidadMedico especialidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISPONIBILIDAD", nullable = false, length = 30)
    private DisponibilidadMedico disponibilidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno,
                           Short edad, String email, String telefono,
                           String cedulaProfesional, EspecialidadMedico especialidad) {
        validarNoEliminado();

        validarDatos(nombre, apellidoPaterno, apellidoMaterno, email,
                telefono, cedulaProfesional, edad);

        actualizarEspecialidad(especialidad);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.email = email.trim().toLowerCase();
        this.telefono = (telefono != null) ? telefono.trim() : null;
        this.cedulaProfesional = cedulaProfesional.trim();
    }

    private void validarNoEliminado (){
        if (this.estadoRegistro == EstadoRegistro.Eliminado)
            throw new IllegalStateException("El medico ya esta eliminado");
    }
    public void actualizarEspecialidad (EspecialidadMedico nuevaEspecialidad) {
        validarNoEliminado ();
        if (nuevaEspecialidad == null)
            throw new IllegalArgumentException("La disponibilidad es requerida");
        this.especialidad = nuevaEspecialidad;
    }
    public void actualizarDisponibilidad (DisponibilidadMedico nuevaDisponibilidad) {
        validarNoEliminado ();
        if (nuevaDisponibilidad == null)
            throw new IllegalArgumentException("La disponibilidad es requerida");
        this.disponibilidad = nuevaDisponibilidad;
    }
    public void eliminar() {
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.Eliminado;
    }

    private void validarDatos (
            String nombre, String apellidoPaterno,
            String apellidoMaterno, String email, String telefono,
            String cedulaProfesional, Short edad ){
        StringCustomUtils.validarTamanio(nombre, 1,50,
                "El nombre es requerido y debe tener entre 4 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoPaterno,1,50,
                "El apellido paterno es requerido y debe tener entre 4 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoMaterno,1,50,
                "El apellido materno es requerido y debe tener entre 4 y 50 caracteres");
        StringCustomUtils.validarTamanio(email,1,100,
                "El email es requerido y debe tener entre 8 y 100 caracteres");
        StringCustomUtils.validarTamanio(telefono,10,10,
                "El telefono es requerido y debe tener 10 caracteres");
        StringCustomUtils.validarTamanio(cedulaProfesional,12,12,
                "La cedula profesional es requerida y debe contener exactamente 12 caracteres");
        ValoresNumericosUtils.validarRangoShort(edad, (short) 18, (short) 100,
                "La edad es requerida y debe estar entre 18 y 100 años");
        if (especialidad == null)
            throw new IllegalArgumentException("La especialidad es requerida");
    }
}
