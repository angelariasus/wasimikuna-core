package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.DetalleRecepcionDTO;
import com.system.wasimikuna.service.DetalleRecepcionService;
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
@RequestMapping("/api/detalles-recepcion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Detalles de Recepción", description = "API para gestión detallada de recepciones de productos")
public class DetalleRecepcionController {

    private final DetalleRecepcionService detalleRecepcionService;

    @Operation(summary = "Obtener todos los detalles de recepción", description = "Recupera una lista de todos los detalles de recepciones de productos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de detalles de recepción obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = DetalleRecepcionDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<DetalleRecepcionDTO>> getAllDetallesRecepcion() {
        List<DetalleRecepcionDTO> detalles = detalleRecepcionService.findAll();
        return ResponseEntity.ok(detalles);
    }

    @Operation(summary = "Obtener detalle de recepción por ID", description = "Recupera un detalle de recepción específico por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle de recepción encontrado",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = DetalleRecepcionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Detalle de recepción no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DetalleRecepcionDTO> getDetalleRecepcionById(
            @Parameter(description = "ID del detalle de recepción", required = true, example = "1") 
            @PathVariable Long id) {
        DetalleRecepcionDTO detalle = detalleRecepcionService.findById(id);
        return ResponseEntity.ok(detalle);
    }

    @Operation(summary = "Obtener detalles por recepción", description = "Recupera todos los detalles de una recepción específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de detalles por recepción obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = DetalleRecepcionDTO.class)))
    })
    @GetMapping("/recepcion/{recepcionId}")
    public ResponseEntity<List<DetalleRecepcionDTO>> getDetallesByRecepcion(
            @Parameter(description = "ID de la recepción", required = true, example = "1") 
            @PathVariable Long recepcionId) {
        List<DetalleRecepcionDTO> detalles = detalleRecepcionService.findByRecepcion(recepcionId);
        return ResponseEntity.ok(detalles);
    }



    @Operation(summary = "Crear nuevo detalle de recepción", description = "Crea un nuevo detalle de recepción de productos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Detalle de recepción creado exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = DetalleRecepcionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<DetalleRecepcionDTO> createDetalleRecepcion(
            @Parameter(description = "Datos del detalle de recepción a crear", required = true)
            @RequestBody DetalleRecepcionDTO detalleDTO) {
        DetalleRecepcionDTO nuevoDetalle = detalleRecepcionService.save(detalleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDetalle);
    }

    @Operation(summary = "Actualizar detalle de recepción", description = "Actualiza los datos de un detalle de recepción existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle de recepción actualizado exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = DetalleRecepcionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Detalle de recepción no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DetalleRecepcionDTO> updateDetalleRecepcion(
            @Parameter(description = "ID del detalle de recepción", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del detalle de recepción", required = true)
            @RequestBody DetalleRecepcionDTO detalleDTO) {
        DetalleRecepcionDTO detalleActualizado = detalleRecepcionService.update(id, detalleDTO);
        return ResponseEntity.ok(detalleActualizado);
    }

    @Operation(summary = "Eliminar detalle de recepción", description = "Elimina un detalle de recepción del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Detalle de recepción eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Detalle de recepción no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalleRecepcion(
            @Parameter(description = "ID del detalle de recepción", required = true, example = "1")
            @PathVariable Long id) {
        detalleRecepcionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}