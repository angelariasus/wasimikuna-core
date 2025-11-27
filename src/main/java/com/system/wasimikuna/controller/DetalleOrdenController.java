package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.DetalleOrdenDTO;
import com.system.wasimikuna.service.DetalleOrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/detalles-orden")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DetalleOrdenController {

    private final DetalleOrdenService detalleOrdenService;

    @GetMapping
    public ResponseEntity<List<DetalleOrdenDTO>> getAllDetalles() {
        List<DetalleOrdenDTO> detalles = detalleOrdenService.findAll();
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleOrdenDTO> getDetalleById(@PathVariable Long id) {
        DetalleOrdenDTO detalle = detalleOrdenService.findById(id);
        return ResponseEntity.ok(detalle);
    }

    @GetMapping("/orden/{ordenCompraId}")
    public ResponseEntity<List<DetalleOrdenDTO>> getDetallesByOrden(@PathVariable Long ordenCompraId) {
        List<DetalleOrdenDTO> detalles = detalleOrdenService.findByOrdenCompra(ordenCompraId);
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/orden/{ordenCompraId}/total")
    public ResponseEntity<BigDecimal> getTotalOrden(@PathVariable Long ordenCompraId) {
        BigDecimal total = detalleOrdenService.calcularTotalOrden(ordenCompraId);
        return ResponseEntity.ok(total);
    }

    @PostMapping
    public ResponseEntity<DetalleOrdenDTO> createDetalle(@RequestBody DetalleOrdenDTO detalleDTO) {
        DetalleOrdenDTO nuevoDetalle = detalleOrdenService.save(detalleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDetalle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleOrdenDTO> updateDetalle(@PathVariable Long id, @RequestBody DetalleOrdenDTO detalleDTO) {
        DetalleOrdenDTO detalleActualizado = detalleOrdenService.update(id, detalleDTO);
        return ResponseEntity.ok(detalleActualizado);
    }

    @PatchMapping("/{id}/cantidad")
    public ResponseEntity<DetalleOrdenDTO> actualizarCantidad(@PathVariable Long id, @RequestParam Integer cantidad) {
        DetalleOrdenDTO detalle = detalleOrdenService.actualizarCantidad(id, cantidad);
        return ResponseEntity.ok(detalle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalle(@PathVariable Long id) {
        detalleOrdenService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}