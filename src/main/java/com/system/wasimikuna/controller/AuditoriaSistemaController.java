package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.AuditoriaSistemaDTO;
import com.system.wasimikuna.service.AuditoriaSistemaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
@Tag(name = "Auditoría del Sistema", description = "API para gestión de auditoría y trazabilidad del sistema")
public class AuditoriaSistemaController {

    private final AuditoriaSistemaService auditoriaService;

    @GetMapping
    public ResponseEntity<List<AuditoriaSistemaDTO>> getAllAuditorias() {
        List<AuditoriaSistemaDTO> auditorias = auditoriaService.findAll();
        return ResponseEntity.ok(auditorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaSistemaDTO> getAuditoriaById(@PathVariable Long id) {
        AuditoriaSistemaDTO auditoria = auditoriaService.findById(id);
        return ResponseEntity.ok(auditoria);
    }

    @GetMapping("/tabla/{tabla}")
    public ResponseEntity<List<AuditoriaSistemaDTO>> getAuditoriasByTabla(@PathVariable String tabla) {
        List<AuditoriaSistemaDTO> auditorias = auditoriaService.findByTablaAfectada(tabla);
        return ResponseEntity.ok(auditorias);
    }

    @GetMapping("/accion/{accion}")
    public ResponseEntity<List<AuditoriaSistemaDTO>> getAuditoriasByAccion(@PathVariable String accion) {
        List<AuditoriaSistemaDTO> auditorias = auditoriaService.findByAccion(accion);
        return ResponseEntity.ok(auditorias);
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<AuditoriaSistemaDTO>> getAuditoriasHoy() {
        List<AuditoriaSistemaDTO> auditorias = auditoriaService.findByFechaHoy();
        return ResponseEntity.ok(auditorias);
    }

    @GetMapping("/estadisticas/accion")
    public ResponseEntity<Map<String, Long>> getEstadisticasPorAccion() {
        Map<String, Long> estadisticas = auditoriaService.getEstadisticasPorAccion();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/estadisticas/tabla")
    public ResponseEntity<Map<String, Long>> getEstadisticasPorTabla() {
        Map<String, Long> estadisticas = auditoriaService.getEstadisticasPorTabla();
        return ResponseEntity.ok(estadisticas);
    }
}