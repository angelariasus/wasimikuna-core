package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.RecetaProductoDTO;
import com.system.wasimikuna.service.RecetaProductoService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
@Tag(name = "Recetas de Productos", description = "API para gestión de recetas y composición de platos con productos")
public class RecetaProductoController {

    private final RecetaProductoService recetaService;

    @Operation(summary = "Obtener todas las recetas", description = "Recupera una lista de todas las recetas de productos disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de recetas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = RecetaProductoDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<RecetaProductoDTO>> getAllRecetas() {
        List<RecetaProductoDTO> recetas = recetaService.findAll();
        return ResponseEntity.ok(recetas);
    }

    @Operation(summary = "Obtener receta por ID", description = "Recupera una receta específica por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Receta encontrada",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = RecetaProductoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecetaProductoDTO> getRecetaById(
            @Parameter(description = "ID de la receta", required = true, example = "1") 
            @PathVariable Long id) {
        RecetaProductoDTO receta = recetaService.findById(id);
        return ResponseEntity.ok(receta);
    }

    @Operation(summary = "Obtener recetas por plato", description = "Recupera todas las recetas de un plato específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de recetas por plato obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = RecetaProductoDTO.class)))
    })
    @GetMapping("/plato/{platoId}")
    public ResponseEntity<List<RecetaProductoDTO>> getRecetasByPlato(
            @Parameter(description = "ID del plato", required = true, example = "1") 
            @PathVariable Long platoId) {
        List<RecetaProductoDTO> recetas = recetaService.findByPlato(platoId);
        return ResponseEntity.ok(recetas);
    }

    @Operation(summary = "Calcular total de ingredientes", description = "Calcula el total de ingredientes necesarios para un plato")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total calculado exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = BigDecimal.class)))
    })
    @GetMapping("/plato/{platoId}/total")
    public ResponseEntity<BigDecimal> getTotalIngredientesPlato(
            @Parameter(description = "ID del plato", required = true, example = "1") 
            @PathVariable Long platoId) {
        BigDecimal total = recetaService.calcularTotalIngredientesPlato(platoId);
        return ResponseEntity.ok(total);
    }

    @Operation(summary = "Crear nueva receta", description = "Crea una nueva receta de producto para un plato")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Receta creada exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = RecetaProductoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<RecetaProductoDTO> createReceta(
            @Parameter(description = "Datos de la receta a crear", required = true)
            @RequestBody RecetaProductoDTO recetaDTO) {
        RecetaProductoDTO nuevaReceta = recetaService.save(recetaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReceta);
    }

    @Operation(summary = "Actualizar receta", description = "Actualiza los datos de una receta existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Receta actualizada exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = RecetaProductoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RecetaProductoDTO> updateReceta(
            @Parameter(description = "ID de la receta", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados de la receta", required = true)
            @RequestBody RecetaProductoDTO recetaDTO) {
        RecetaProductoDTO recetaActualizada = recetaService.update(id, recetaDTO);
        return ResponseEntity.ok(recetaActualizada);
    }

    @Operation(summary = "Eliminar receta", description = "Elimina una receta del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Receta eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceta(
            @Parameter(description = "ID de la receta", required = true, example = "1")
            @PathVariable Long id) {
        recetaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}