package com.francisco.commons.enums;

import lombok.Getter;

@Getter
public enum EspecialidadMedico {

    MEDICINA_GENERAL(1, "Medicina General"),
    PEDIATRIA(2, "Pediatría"),
    CARDIOLOGIA(3, "Cardiología"),
    DERMATOLOGIA(4, "Dermatología"),
    NEUROLOGIA(5, "Neurología"),
    GINECOLOGIA_Y_OBSTETRICIA(6, "Ginecología y Obstetricia"),
    PSIQUIATRIA(7, "Psiquiatría"),
    TRAUMATOLOGIA_Y_ORTOPEDIA(8, "Traumatología y Ortopedia"),
    ONCOLOGIA(9, "Oncología"),
    OTORRINOLARINGOLOGIA(10, "Otorrilaringología"),
    OFTALMOLOGIA(11, "Oftalmología"),
    ENDOCRINOLOGIA(12, "Endocrinología"),
    NEFROLOGIA(13, "Nefrología"),
    REUMATOLOGIA(14, "Reumatología"),
    UROLOGIA(15, "Urología");

    private final Integer id;
    private final String descripcion;

    EspecialidadMedico(Integer id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public static EspecialidadMedico buscarPorId(Integer id) {
        for (EspecialidadMedico especialidadMedico : values()) {
            if (especialidadMedico.getId().equals(id)) {
                return especialidadMedico;
            }
        }
        throw new IllegalArgumentException("La especialidad médica proporcionada no existe en el catálogo.");
    }
    public static EspecialidadMedico obtenerEspecialidadPorCodigo(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de especialidad no puede ser nulo.");
        }
        return buscarPorId(id.intValue());
    }
    }
