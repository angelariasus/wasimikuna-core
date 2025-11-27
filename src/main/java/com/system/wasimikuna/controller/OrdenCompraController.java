package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.OrdenCompraDTO;
import com.system.wasimikuna.service.OrdenCompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-compra")
@RequiredArgsConstructor
@Tag(name = "Órdenes de Compra", description = "API para gestión de órdenes de compra de productos")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    @Operation(summary = "Obtener todas las órdenes de compra", description = "Recupera una lista de todas las órdenes de compra")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = OrdenCompraDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<OrdenCompraDTO>> getAllOrdenes() {
        List<OrdenCompraDTO> ordenes = ordenCompraService.findAll();
        return ResponseEntity.ok(ordenes);
    }

    @Operation(summary = "Obtener orden de compra por ID", description = "Recupera una orden de compra específica por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden de compra encontrada",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = OrdenCompraDTO.class))),
        @ApiResponse(responseCode = "404", description = "Orden de compra no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompraDTO> getOrdenById(
            @Parameter(description = "ID de la orden de compra", required = true, example = "1") 
            @PathVariable Long id) {
        OrdenCompraDTO orden = ordenCompraService.findById(id);
        return ResponseEntity.ok(orden);
    }

    @GetMapping("/afiliado/{afiliadoId}")
    public ResponseEntity<List<OrdenCompraDTO>> getOrdenesByAfiliado(@PathVariable Long afiliadoId) {
        List<OrdenCompraDTO> ordenes = ordenCompraService.findByAfiliado(afiliadoId);
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<OrdenCompraDTO>> getOrdenesByEstado(@PathVariable Integer estado) {
        List<OrdenCompraDTO> ordenes = ordenCompraService.findByEstado(estado);
        return ResponseEntity.ok(ordenes);
    }

    @PostMapping
    public ResponseEntity<OrdenCompraDTO> createOrden(@RequestBody OrdenCompraDTO ordenDTO) {
        OrdenCompraDTO nuevaOrden = ordenCompraService.save(ordenDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaOrden);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenCompraDTO> updateOrden(@PathVariable Long id, @RequestBody OrdenCompraDTO ordenDTO) {
        OrdenCompraDTO ordenActualizada = ordenCompraService.update(id, ordenDTO);
        return ResponseEntity.ok(ordenActualizada);
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<OrdenCompraDTO> aprobarOrden(@PathVariable Long id) {
        OrdenCompraDTO orden = ordenCompraService.cambiarEstado(id, 1); // APROBADA
        return ResponseEntity.ok(orden);
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<OrdenCompraDTO> completarOrden(@PathVariable Long id) {
        OrdenCompraDTO orden = ordenCompraService.cambiarEstado(id, 3); // COMPLETADA
        return ResponseEntity.ok(orden);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrden(@PathVariable Long id) {
        ordenCompraService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}