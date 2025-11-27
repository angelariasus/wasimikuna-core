package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.RecepcionDTO;
import com.system.wasimikuna.service.RecepcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Recepciones", description = "API para gestión de recepciones de productos")
public class RecepcionController {

    private final RecepcionService recepcionService;

    @Operation(summary = "Obtener todas las recepciones", description = "Recupera una lista de todas las recepciones de productos")
    @GetMapping
    public ResponseEntity<List<RecepcionDTO>> getAllRecepciones() {
        List<RecepcionDTO> recepciones = recepcionService.findAll();
        return ResponseEntity.ok(recepciones);
    }

    @Operation(summary = "Obtener recepción por ID", description = "Recupera una recepción específica por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<RecepcionDTO> getRecepcionById(@PathVariable Long id) {
        RecepcionDTO recepcion = recepcionService.findById(id);
        return ResponseEntity.ok(recepcion);
    }

    @GetMapping("/envio/{envioId}")
    public ResponseEntity<List<RecepcionDTO>> getRecepcionesByEnvio(@PathVariable Long envioId) {
        List<RecepcionDTO> recepciones = recepcionService.findByEnvio(envioId);
        return ResponseEntity.ok(recepciones);
    }

    @PostMapping
    public ResponseEntity<RecepcionDTO> createRecepcion(@RequestBody RecepcionDTO recepcionDTO) {
        RecepcionDTO nuevaRecepcion = recepcionService.save(recepcionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaRecepcion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecepcionDTO> updateRecepcion(@PathVariable Long id, @RequestBody RecepcionDTO recepcionDTO) {
        RecepcionDTO recepcionActualizada = recepcionService.update(id, recepcionDTO);
        return ResponseEntity.ok(recepcionActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecepcion(@PathVariable Long id) {
        recepcionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}