package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.IncidenciaSanitariaDTO;
import com.system.wasimikuna.service.IncidenciaSanitariaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
@Tag(name = "Incidencias Sanitarias", description = "API para gestión de incidencias sanitarias en el sistema alimentario")
public class IncidenciaSanitariaController {

    private final IncidenciaSanitariaService incidenciaService;

    @GetMapping
    public ResponseEntity<List<IncidenciaSanitariaDTO>> getAllIncidencias() {
        List<IncidenciaSanitariaDTO> incidencias = incidenciaService.findAll();
        return ResponseEntity.ok(incidencias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidenciaSanitariaDTO> getIncidenciaById(@PathVariable Long id) {
        IncidenciaSanitariaDTO incidencia = incidenciaService.findById(id);
        return ResponseEntity.ok(incidencia);
    }

    @GetMapping("/institucion/{institucionId}")
    public ResponseEntity<List<IncidenciaSanitariaDTO>> getIncidenciasByInstitucion(@PathVariable Long institucionId) {
        List<IncidenciaSanitariaDTO> incidencias = incidenciaService.findByInstitucion(institucionId);
        return ResponseEntity.ok(incidencias);
    }

    @PostMapping
    public ResponseEntity<IncidenciaSanitariaDTO> createIncidencia(@RequestBody IncidenciaSanitariaDTO incidenciaDTO) {
        IncidenciaSanitariaDTO nuevaIncidencia = incidenciaService.save(incidenciaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaIncidencia);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidenciaSanitariaDTO> updateIncidencia(@PathVariable Long id, @RequestBody IncidenciaSanitariaDTO incidenciaDTO) {
        IncidenciaSanitariaDTO incidenciaActualizada = incidenciaService.update(id, incidenciaDTO);
        return ResponseEntity.ok(incidenciaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncidencia(@PathVariable Long id) {
        incidenciaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}