package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.ProductoDTO;
import com.system.wasimikuna.service.ProductoService;
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
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para gestión de productos alimenticios")
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Obtener todos los productos", description = "Recupera una lista de todos los productos disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ProductoDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        List<ProductoDTO> productos = productoService.findAll();
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener producto por ID", description = "Recupera un producto específico por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ProductoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(
            @Parameter(description = "ID del producto", required = true, example = "1") 
            @PathVariable Long id) {
        ProductoDTO producto = productoService.findById(id);
        return ResponseEntity.ok(producto);
    }

    @Operation(summary = "Obtener productos por categoría", description = "Recupera todos los productos de una categoría específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos por categoría obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ProductoDTO.class)))
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoDTO>> getProductosByCategoria(
            @Parameter(description = "Categoría del producto", required = true, example = "LACTEOS") 
            @PathVariable String categoria) {
        List<ProductoDTO> productos = productoService.findByCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ProductoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<ProductoDTO> createProducto(
            @Parameter(description = "Datos del producto a crear", required = true)
            @RequestBody ProductoDTO productoDTO) {
        ProductoDTO nuevoProducto = productoService.save(productoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ProductoDTO.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> updateProducto(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del producto", required = true)
            @RequestBody ProductoDTO productoDTO) {
        ProductoDTO productoActualizado = productoService.update(id, productoDTO);
        return ResponseEntity.ok(productoActualizado);
    }



    @Operation(summary = "Eliminar producto", description = "Elimina un producto del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}