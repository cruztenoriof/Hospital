package com.francisco.pacientes.entity;


import com.francisco.commons.enums.EstadoRegistro;
import com.francisco.commons.utils.StringCustomUtils;
import com.francisco.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "PACIENTES")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "PESO", nullable = false)
    private Double peso;

    @Column(name = "ESTATURA", nullable = false)
    private Double estatura;

    @Column(name = "IMC", nullable = false)
    private Double imc;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "TELEFONO", nullable = false, length = 10)
    private String telefono;

    @Column(name = "DIRECCION", nullable = false, length = 150)
    private String direccion;

    @Column(name = "NUM_EXPEDIENTE", nullable = false, length = 20)
    private String numExpediente;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EstadoRegistro estadoRegistro;

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno,
                           Short edad, Double peso, Double estatura, String email,
                           String telefono, String direccion ) {
        validarNoEliminado();

        validarDatos(nombre, apellidoPaterno, apellidoMaterno, email, telefono, direccion,
                peso, edad, estatura);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.asignarImc();
        this.email = email.trim().toLowerCase();
        this.telefono = telefono.trim();
        this.direccion = direccion.trim();
        this.asignarNumExpediente();
    }

    private void validarDatos (
            String nombre, String apellidoPaterno,
            String apellidoMaterno, String email, String telefono, String direccion,
            Double peso, Short edad, Double estatura ){
        StringCustomUtils.validarTamanio(nombre, 1,50,
                "El nombre es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoPaterno,1,50,
                "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoMaterno,1,50,
                "El apellido materno es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(email,1,100,
                "El email es requerido y debe tener entre 1 y 100 caracteres");
        StringCustomUtils.validarTamanio(telefono,10,10,
                "El telefono es requerido y debe tener 10 caracteres");
        StringCustomUtils.validarTamanio(direccion,1,150,
                "La direccion es requeridad y debe tener entre 1 y 150 caracteres");
        ValoresNumericosUtils.validarRangoShort(edad, (short) 1, (short) 100,
                "La edad es requerida y debe estar entre 1 y 100 años");
        ValoresNumericosUtils.validarRangoDouble(peso,1.0,200.0,
                "El peso es requerido y debe ser entre 1 a 200 kg");
        ValoresNumericosUtils.validarRangoDouble(estatura,1.0,2.0,
                "La estatura es requerida y debe ser de 1 a 2 metros");
    }
    private void validarNoEliminado (){
        if (this.estadoRegistro == EstadoRegistro.Eliminado)
            throw new IllegalArgumentException(
                    "El paciente está elimiado");
    }
    public void asignarImc (){
        validarNoEliminado ();

        if (peso == null || estatura == null || estatura <= 0){
    this.imc = 0.0;
    return;
        }
        this.imc= this.peso / (this.estatura * this.estatura);
    }
    public void asignarNumExpediente (){
        validarNoEliminado();
        if (telefono == null){
            this.numExpediente = null;
            return;
        }
        StringBuilder expediente= new StringBuilder(telefono.length()*2);
        for (char c:this.telefono.toCharArray())
            expediente.append(c).append("X");
        this.numExpediente = expediente.toString();
    }
    public void eliminar (){
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.Eliminado;
    }
}
