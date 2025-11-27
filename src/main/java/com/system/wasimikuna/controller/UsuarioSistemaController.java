package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.UsuarioSistemaDTO;
import com.system.wasimikuna.service.UsuarioSistemaService;
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
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios del Sistema", description = "API para gestión de usuarios del sistema")
public class UsuarioSistemaController {

    private final UsuarioSistemaService usuarioService;

    @Operation(summary = "Obtener todos los usuarios", description = "Recupera una lista de todos los usuarios del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = UsuarioSistemaDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<UsuarioSistemaDTO>> getAllUsuarios() {
        List<UsuarioSistemaDTO> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Recupera un usuario específico por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = UsuarioSistemaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSistemaDTO> getUsuarioById(
            @Parameter(description = "ID del usuario", required = true, example = "1") 
            @PathVariable Long id) {
        UsuarioSistemaDTO usuario = usuarioService.findById(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Obtener usuarios por rol", description = "Recupera todos los usuarios que tienen un rol específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios por rol obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = UsuarioSistemaDTO.class)))
    })
    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<UsuarioSistemaDTO>> getUsuariosByRol(
            @Parameter(description = "ID del rol", required = true, example = "1") 
            @PathVariable Integer rolId) {
        List<UsuarioSistemaDTO> usuarios = usuarioService.findByRol(rolId);
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = UsuarioSistemaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<UsuarioSistemaDTO> createUsuario(
            @Parameter(description = "Datos del usuario a crear", required = true)
            @RequestBody UsuarioSistemaDTO usuarioDTO) {
        UsuarioSistemaDTO nuevoUsuario = usuarioService.save(usuarioDTO, "defaultPassword123");
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = UsuarioSistemaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioSistemaDTO> updateUsuario(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long id, 
            @Parameter(description = "Datos actualizados del usuario", required = true)
            @RequestBody UsuarioSistemaDTO usuarioDTO) {
        UsuarioSistemaDTO usuarioActualizado = usuarioService.update(id, usuarioDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @Operation(summary = "Cambiar estado del usuario", description = "Activa o desactiva un usuario del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del usuario cambiado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/{id}/toggle-estado")
    public ResponseEntity<Void> toggleEstadoUsuario(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long id) {
        usuarioService.toggleEstado(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}