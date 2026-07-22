package com.francisco.commons.enums;


import lombok.Getter;

@Getter
public enum DisponibilidadMedico {
    DISPONIBLE(1, "Disponible"),
    EN_CONSULTA(2, "En Consulta"),
    FUERA_DE_TURNO(3, "Fuera_de_turno"),
    NO_DISPONIBLE(4, "No disponible");

    private final Integer id;
    private final String codigo;
    private final String descripcion;

    DisponibilidadMedico(Integer id, String descripcion) {
        this.id = id;
        this.codigo = String.valueOf(id);
        this.descripcion = descripcion;
    }
    public static DisponibilidadMedico obtenerDisponibilidadporCodigo(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de disponibilidad no puede ser nulo.");
        }
        return buscarPorCodigo(String.valueOf(id));
    }
    public static DisponibilidadMedico buscarPorCodigo(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException("El código de disponibilidad no puede ser nulo.");
        }
        for (DisponibilidadMedico disp : values()) {
            if (disp.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return disp;
            }
        }
        throw new IllegalArgumentException("El código de disponibilidad proporcionado no existe: " + codigo);
    }
}