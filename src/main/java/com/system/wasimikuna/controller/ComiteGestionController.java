package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.ComiteGestionDTO;
import com.system.wasimikuna.service.ComiteGestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComiteGestionController {

    private final ComiteGestionService comiteService;

    @GetMapping
    public ResponseEntity<List<ComiteGestionDTO>> getAllComites() {
        List<ComiteGestionDTO> comites = comiteService.findAll();
        return ResponseEntity.ok(comites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComiteGestionDTO> getComiteById(@PathVariable Long id) {
        ComiteGestionDTO comite = comiteService.findById(id);
        return ResponseEntity.ok(comite);
    }

    @PostMapping
    public ResponseEntity<ComiteGestionDTO> createComite(@RequestBody ComiteGestionDTO comiteDTO) {
        ComiteGestionDTO nuevoComite = comiteService.save(comiteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoComite);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComiteGestionDTO> updateComite(@PathVariable Long id, @RequestBody ComiteGestionDTO comiteDTO) {
        ComiteGestionDTO comiteActualizado = comiteService.update(id, comiteDTO);
        return ResponseEntity.ok(comiteActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComite(@PathVariable Long id) {
        comiteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}