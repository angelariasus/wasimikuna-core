package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.InstitucionEducativaDTO;
import com.system.wasimikuna.service.InstitucionEducativaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instituciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Instituciones Educativas", description = "API para gestión de instituciones educativas beneficiarias")
public class InstitucionEducativaController {

    private final InstitucionEducativaService institucionService;

    @GetMapping
    public ResponseEntity<List<InstitucionEducativaDTO>> getAllInstituciones() {
        List<InstitucionEducativaDTO> instituciones = institucionService.findAll();
        return ResponseEntity.ok(instituciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstitucionEducativaDTO> getInstitucionById(@PathVariable Long id) {
        InstitucionEducativaDTO institucion = institucionService.findById(id);
        return ResponseEntity.ok(institucion);
    }

    @PostMapping
    public ResponseEntity<InstitucionEducativaDTO> createInstitucion(@RequestBody InstitucionEducativaDTO institucionDTO) {
        InstitucionEducativaDTO nuevaInstitucion = institucionService.save(institucionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaInstitucion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstitucionEducativaDTO> updateInstitucion(@PathVariable Long id, @RequestBody InstitucionEducativaDTO institucionDTO) {
        InstitucionEducativaDTO institucionActualizada = institucionService.update(id, institucionDTO);
        return ResponseEntity.ok(institucionActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstitucion(@PathVariable Long id) {
        institucionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}