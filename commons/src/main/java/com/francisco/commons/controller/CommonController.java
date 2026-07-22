package com.francisco.commons.controller;

import com.francisco.commons.services.CrudService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Validated
public class CommonController <RQ, RS, S extends CrudService<RS, RQ>> {
    protected final S service;
    @GetMapping
    public ResponseEntity<List<RS>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RS> obtenerPorId(
            @PathVariable @Positive(message = "EL IDE DEBER SER POSITIVO") Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<RS> registar(
            @Valid @RequestBody RQ request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RS> actualizar(
            @PathVariable @Positive (message = "EL ID DEBE SER POSITIVO") Long id,
            @Valid @RequestBody RQ request){
        return ResponseEntity.ok(service.actualizar(request,id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive (message = "EL ID DEBE SER POSITIVO") Long id){
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
