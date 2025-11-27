package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.PlatoDTO;
import com.system.wasimikuna.service.PlatoService;
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

import java.util.List;

@RestController
@RequestMapping("/api/platos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Platos", description = "API para gestión de platos y menús")
public class PlatoController {

    private final PlatoService platoService;

    @Operation(summary = "Obtener todos los platos", description = "Recupera una lista de todos los platos disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de platos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = PlatoDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<PlatoDTO>> getAllPlatos() {
        List<PlatoDTO> platos = platoService.findAll();
        return ResponseEntity.ok(platos);
    }

    @Operation(summary = "Obtener plato por ID", description = "Recupera un plato específico por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plato encontrado",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = PlatoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlatoDTO> getPlatoById(
            @Parameter(description = "ID del plato", required = true, example = "1") 
            @PathVariable Long id) {
        PlatoDTO plato = platoService.findById(id);
        return ResponseEntity.ok(plato);
    }

    @Operation(summary = "Crear nuevo plato", description = "Crea un nuevo plato en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plato creado exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = PlatoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<PlatoDTO> createPlato(
            @Parameter(description = "Datos del plato a crear", required = true)
            @RequestBody PlatoDTO platoDTO) {
        PlatoDTO nuevoPlato = platoService.save(platoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPlato);
    }

    @Operation(summary = "Actualizar plato", description = "Actualiza los datos de un plato existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plato actualizado exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = PlatoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Plato no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PlatoDTO> updatePlato(
            @Parameter(description = "ID del plato", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del plato", required = true)
            @RequestBody PlatoDTO platoDTO) {
        PlatoDTO platoActualizado = platoService.update(id, platoDTO);
        return ResponseEntity.ok(platoActualizado);
    }

    @Operation(summary = "Eliminar plato", description = "Elimina un plato del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Plato eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlato(
            @Parameter(description = "ID del plato", required = true, example = "1")
            @PathVariable Long id) {
        platoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}