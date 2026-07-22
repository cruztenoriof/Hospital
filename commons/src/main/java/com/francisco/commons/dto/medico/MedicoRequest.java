package com.francisco.commons.dto.medico;

import jakarta.validation.constraints.*;

public record MedicoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min =3, max = 50, message = "El nombre no puede exceder los 50 caracteres")
        String nombre,

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(min =3, max = 50, message = "El apellido paterno no puede exceder los 50 caracteres")
        String apellidoPaterno,

        @NotBlank(message = "El apellido materno es obligatorio")
        @Size(min = 3, max = 50, message = "El apellido materno no puede exceder los 50 caracteres")
        String apellidoMaterno,

        @NotNull(message = "La edad minima es de 18 años")
        @Min(value = 18, message = "La edad mínima es de 18 años.")
        @Max(value = 100, message = "La edad máxima es de 100 años.")
        Short edad,

        @NotBlank(message = "El email es requerido")
        @Size (min =8, max = 100, message = "La email debe tener entre 8 y 100 caracteres")
        @Email(message = "El email debe tener un formato válido (ejemplo@dominio.com)")
        String email,

        @NotBlank(message = "El telefono es requerido")
        @Pattern(regexp = "^[0-9]{10}$", message = "El telefono debe contener solo 10 digitos")
        String telefono,

        @NotBlank(message = "La cedula profesional es obligatoria")
        @Size(min = 12, max = 12, message = "La cedula profesional debe exactamente 12 caracteres")
        String cedulaProfesional,

        @NotNull(message = "El ID de especialidad es requerido")
        @Positive (message = "El ID de la especialidad deber ser positivo")
        Long idEspecialidad
) {}
