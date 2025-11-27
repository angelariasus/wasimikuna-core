package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.RolDTO;
import com.system.wasimikuna.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "API para gestión de roles del sistema")
public class RolController {

    private final RolService rolService;

    @Operation(summary = "Obtener todos los roles", description = "Recupera una lista de todos los roles del sistema")
    @GetMapping
    public ResponseEntity<List<RolDTO>> getAllRoles() {
        List<RolDTO> roles = rolService.findAll();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Obtener rol por ID", description = "Recupera un rol específico por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> getRolById(@PathVariable Integer id) {
        RolDTO rol = rolService.findById(id);
        return ResponseEntity.ok(rol);
    }

    @Operation(summary = "Crear nuevo rol", description = "Crea un nuevo rol en el sistema")
    @PostMapping
    public ResponseEntity<RolDTO> createRol(@RequestBody RolDTO rolDTO) {
        RolDTO nuevoRol = rolService.save(rolDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol);
    }

    @Operation(summary = "Actualizar rol", description = "Actualiza los datos de un rol existente")
    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> updateRol(@PathVariable Integer id, @RequestBody RolDTO rolDTO) {
        RolDTO rolActualizado = rolService.update(id, rolDTO);
        return ResponseEntity.ok(rolActualizado);
    }

    @Operation(summary = "Eliminar rol", description = "Elimina un rol del sistema")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Integer id) {
        rolService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}