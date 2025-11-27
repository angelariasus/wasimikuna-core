package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.ProgramacionMenuDTO;
import com.system.wasimikuna.service.ProgramacionMenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/programaciones-menu")
@RequiredArgsConstructor
@Tag(name = "Programación de Menús", description = "API para gestión de programación de menús por institución")
public class ProgramacionMenuController {

    private final ProgramacionMenuService programacionService;

    @GetMapping
    public ResponseEntity<List<ProgramacionMenuDTO>> getAllProgramaciones() {
        List<ProgramacionMenuDTO> programaciones = programacionService.findAll();
        return ResponseEntity.ok(programaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramacionMenuDTO> getProgramacionById(@PathVariable Long id) {
        ProgramacionMenuDTO programacion = programacionService.findById(id);
        return ResponseEntity.ok(programacion);
    }

    @GetMapping("/institucion/{institucionId}")
    public ResponseEntity<List<ProgramacionMenuDTO>> getProgramacionesByInstitucion(@PathVariable Long institucionId) {
        List<ProgramacionMenuDTO> programaciones = programacionService.findByInstitucion(institucionId);
        return ResponseEntity.ok(programaciones);
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<ProgramacionMenuDTO>> getProgramacionesByFecha(@PathVariable LocalDate fecha) {
        List<ProgramacionMenuDTO> programaciones = programacionService.findByFechaConsumo(fecha);
        return ResponseEntity.ok(programaciones);
    }

    @PostMapping
    public ResponseEntity<ProgramacionMenuDTO> createProgramacion(@RequestBody ProgramacionMenuDTO programacionDTO) {
        ProgramacionMenuDTO nuevaProgramacion = programacionService.save(programacionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaProgramacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramacionMenuDTO> updateProgramacion(@PathVariable Long id, @RequestBody ProgramacionMenuDTO programacionDTO) {
        ProgramacionMenuDTO programacionActualizada = programacionService.update(id, programacionDTO);
        return ResponseEntity.ok(programacionActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgramacion(@PathVariable Long id) {
        programacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}