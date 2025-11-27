package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.AfiliadoDTO;
import com.system.wasimikuna.service.AfiliadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/afiliados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AfiliadoController {

    private final AfiliadoService afiliadoService;

    @GetMapping
    public ResponseEntity<List<AfiliadoDTO>> getAllAfiliados() {
        List<AfiliadoDTO> afiliados = afiliadoService.findAll();
        return ResponseEntity.ok(afiliados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AfiliadoDTO> getAfiliadoById(@PathVariable Long id) {
        AfiliadoDTO afiliado = afiliadoService.findById(id);
        return ResponseEntity.ok(afiliado);
    }

    @PostMapping
    public ResponseEntity<AfiliadoDTO> createAfiliado(@RequestBody AfiliadoDTO afiliadoDTO) {
        AfiliadoDTO nuevoAfiliado = afiliadoService.save(afiliadoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoAfiliado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AfiliadoDTO> updateAfiliado(@PathVariable Long id, @RequestBody AfiliadoDTO afiliadoDTO) {
        AfiliadoDTO afiliadoActualizado = afiliadoService.update(id, afiliadoDTO);
        return ResponseEntity.ok(afiliadoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAfiliado(@PathVariable Long id) {
        afiliadoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}