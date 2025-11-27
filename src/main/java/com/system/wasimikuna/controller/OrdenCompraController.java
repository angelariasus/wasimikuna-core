package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.OrdenCompraDTO;
import com.system.wasimikuna.service.OrdenCompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-compra")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    @GetMapping
    public ResponseEntity<List<OrdenCompraDTO>> getAllOrdenes() {
        List<OrdenCompraDTO> ordenes = ordenCompraService.findAll();
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompraDTO> getOrdenById(@PathVariable Long id) {
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