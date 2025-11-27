package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.PlatoDTO;
import com.system.wasimikuna.service.PlatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlatoController {

    private final PlatoService platoService;

    @GetMapping
    public ResponseEntity<List<PlatoDTO>> getAllPlatos() {
        List<PlatoDTO> platos = platoService.findAll();
        return ResponseEntity.ok(platos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatoDTO> getPlatoById(@PathVariable Long id) {
        PlatoDTO plato = platoService.findById(id);
        return ResponseEntity.ok(plato);
    }



    @PostMapping
    public ResponseEntity<PlatoDTO> createPlato(@RequestBody PlatoDTO platoDTO) {
        PlatoDTO nuevoPlato = platoService.save(platoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPlato);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatoDTO> updatePlato(@PathVariable Long id, @RequestBody PlatoDTO platoDTO) {
        PlatoDTO platoActualizado = platoService.update(id, platoDTO);
        return ResponseEntity.ok(platoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlato(@PathVariable Long id) {
        platoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}