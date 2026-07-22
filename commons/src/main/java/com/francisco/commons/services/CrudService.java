package com.francisco.commons.services;

import java.util.List;

public interface CrudService<RS, RQ> {
    List<RS> listar();
    RS obtenerPorId (Long id);
    RS registar (RQ request);
    RS actualizar (RQ request, Long id);
    void eliminar (Long id);
}
