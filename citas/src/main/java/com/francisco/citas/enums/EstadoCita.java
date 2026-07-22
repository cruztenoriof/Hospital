package com.francisco.citas.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Getter
public enum EstadoCita {

    CANCELADA(5L, "Cita cancelada", false, true){
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of();
        }
    },
    CONFIRMADA(2L, "Confrimada por el paciente", true, false) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return EnumSet.of(EN_CURSO, CANCELADA);
        }
    },
    EN_CURSO(3L, "Paciente llegó a su cita", false, false){
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return EnumSet.of(FINALIZADA);
        }
    },
    FINALIZADA(4L, "Cita finalziada", false, true){
        @Override
        public Set<EstadoCita> puedeCambiar() {
        return Set.of();
    }
    },
    PENDIENTE(1L, "Pendiente de confirmar", true, true) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of(CONFIRMADA, CANCELADA);
        }
    };

    private final Long codigo;

    private final String descripcion;

    private final boolean actualizable;

    private final boolean eliminable;

    public abstract Set<EstadoCita> puedeCambiar();

    public boolean puedeCambiarA(EstadoCita nuevoEstado){
        return this.puedeCambiar().contains(nuevoEstado);
    }

    public static EstadoCita obtenerCitaporCodigo(Long codigo) {
        for (EstadoCita e : values()) {
            if (Objects.equals(e.codigo, codigo)) {
                return e;

            }
        }
        throw new IllegalArgumentException("Codigo de cita no valido: " + codigo);
    }
}
