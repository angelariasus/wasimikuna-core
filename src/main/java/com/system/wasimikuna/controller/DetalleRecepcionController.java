package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.DetalleRecepcionDTO;
import com.system.wasimikuna.service.DetalleRecepcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-recepcion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DetalleRecepcionController {

    private final DetalleRecepcionService detalleRecepcionService;

    @GetMapping
    public ResponseEntity<List<DetalleRecepcionDTO>> getAllDetallesRecepcion() {
        List<DetalleRecepcionDTO> detalles = detalleRecepcionService.findAll();
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleRecepcionDTO> getDetalleRecepcionById(@PathVariable Long id) {
        DetalleRecepcionDTO detalle = detalleRecepcionService.findById(id);
        return ResponseEntity.ok(detalle);
    }

    @GetMapping("/recepcion/{recepcionId}")
    public ResponseEntity<List<DetalleRecepcionDTO>> getDetallesByRecepcion(@PathVariable Long recepcionId) {
        List<DetalleRecepcionDTO> detalles = detalleRecepcionService.findByRecepcion(recepcionId);
        return ResponseEntity.ok(detalles);
    }



    @PostMapping
    public ResponseEntity<DetalleRecepcionDTO> createDetalleRecepcion(@RequestBody DetalleRecepcionDTO detalleDTO) {
        DetalleRecepcionDTO nuevoDetalle = detalleRecepcionService.save(detalleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDetalle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleRecepcionDTO> updateDetalleRecepcion(@PathVariable Long id, @RequestBody DetalleRecepcionDTO detalleDTO) {
        DetalleRecepcionDTO detalleActualizado = detalleRecepcionService.update(id, detalleDTO);
        return ResponseEntity.ok(detalleActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalleRecepcion(@PathVariable Long id) {
        detalleRecepcionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}