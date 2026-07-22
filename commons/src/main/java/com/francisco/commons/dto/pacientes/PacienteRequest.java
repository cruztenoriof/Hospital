package com.francisco.commons.dto.pacientes;

import jakarta.validation.constraints.*;

public  record  PacienteRequest (
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min =1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(min =1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @NotBlank(message = "El apellido materno es obligatorio")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @NotNull(message = "La edad es requerida")
        @Min(value = 1, message = "La edad mínima es de 18 años.")
        @Max(value = 100, message = "La edad máxima es de 100 años.")
        Short edad,

        @NotNull(message = "El peso es requerido")
        @DecimalMin(value = "1.0", message = "El peso debe ser mayor a 1kg")
        @DecimalMax(value = "200", message = "El peso máximo permitido es de 200 kg.")
        Double peso,

        @NotNull(message = "La estatura es requerida")
        @DecimalMin(value = "1.0", message = "La estatura debe ser mayor que 1 metro")
        @DecimalMax(value = "2.0", message = "La estatura máxima permitida es de 2 metros")
        Double estatura,

        @NotBlank(message = "El email es requerido")
        @Size (min =1, max = 100, message = "La email debe tener entre 1 y 100 caracteres")
        @Email(message = "El email debe tener un formato válido (ejemplo@dominio.com)")
        String email,

        @NotBlank(message = "El telefono es requerido")
        @Pattern(regexp = "^[0-9]{10}$", message = "El telefono debe contener solo 10 digitos númericos")
        String telefono,

        @NotBlank(message = "La direccion es requerida")
        @Size (min =1, max = 150, message = "La direccion debe tener entre 1 y 50 caracteres")
        String direccion
) {}
