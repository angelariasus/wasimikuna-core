package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.DetalleOrdenDTO;
import com.system.wasimikuna.service.DetalleOrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/detalles-orden")
@RequiredArgsConstructor
@Tag(name = "Detalles de Orden", description = "API para gestión de detalles de órdenes de compra")
public class DetalleOrdenController {

    private final DetalleOrdenService detalleOrdenService;

    @Operation(summary = "Obtener todos los detalles de órdenes", description = "Recupera una lista de todos los detalles de órdenes de compra")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de detalles obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = DetalleOrdenDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<DetalleOrdenDTO>> getAllDetalles() {
        List<DetalleOrdenDTO> detalles = detalleOrdenService.findAll();
        return ResponseEntity.ok(detalles);
    }

    @Operation(summary = "Obtener detalle por ID", description = "Recupera un detalle de orden específico por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<DetalleOrdenDTO> getDetalleById(@PathVariable Long id) {
        DetalleOrdenDTO detalle = detalleOrdenService.findById(id);
        return ResponseEntity.ok(detalle);
    }

    @Operation(summary = "Obtener detalles por orden", description = "Recupera todos los detalles de una orden de compra específica")
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