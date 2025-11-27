package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.RecetaProductoDTO;
import com.system.wasimikuna.service.RecetaProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecetaProductoController {

    private final RecetaProductoService recetaService;

    @GetMapping
    public ResponseEntity<List<RecetaProductoDTO>> getAllRecetas() {
        List<RecetaProductoDTO> recetas = recetaService.findAll();
        return ResponseEntity.ok(recetas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaProductoDTO> getRecetaById(@PathVariable Long id) {
        RecetaProductoDTO receta = recetaService.findById(id);
        return ResponseEntity.ok(receta);
    }

    @GetMapping("/plato/{platoId}")
    public ResponseEntity<List<RecetaProductoDTO>> getRecetasByPlato(@PathVariable Long platoId) {
        List<RecetaProductoDTO> recetas = recetaService.findByPlato(platoId);
        return ResponseEntity.ok(recetas);
    }

    @GetMapping("/plato/{platoId}/total")
    public ResponseEntity<BigDecimal> getTotalIngredientesPlato(@PathVariable Long platoId) {
        BigDecimal total = recetaService.calcularTotalIngredientesPlato(platoId);
        return ResponseEntity.ok(total);
    }

    @PostMapping
    public ResponseEntity<RecetaProductoDTO> createReceta(@RequestBody RecetaProductoDTO recetaDTO) {
        RecetaProductoDTO nuevaReceta = recetaService.save(recetaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReceta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecetaProductoDTO> updateReceta(@PathVariable Long id, @RequestBody RecetaProductoDTO recetaDTO) {
        RecetaProductoDTO recetaActualizada = recetaService.update(id, recetaDTO);
        return ResponseEntity.ok(recetaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceta(@PathVariable Long id) {
        recetaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}