package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.UsuarioSistemaDTO;
import com.system.wasimikuna.service.UsuarioSistemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioSistemaController {

    private final UsuarioSistemaService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioSistemaDTO>> getAllUsuarios() {
        List<UsuarioSistemaDTO> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSistemaDTO> getUsuarioById(@PathVariable Long id) {
        UsuarioSistemaDTO usuario = usuarioService.findById(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<UsuarioSistemaDTO>> getUsuariosByRol(@PathVariable Integer rolId) {
        List<UsuarioSistemaDTO> usuarios = usuarioService.findByRol(rolId);
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<UsuarioSistemaDTO> createUsuario(@RequestBody UsuarioSistemaDTO usuarioDTO) {
        UsuarioSistemaDTO nuevoUsuario = usuarioService.save(usuarioDTO, "defaultPassword123");
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioSistemaDTO> updateUsuario(
            @PathVariable Long id, 
            @RequestBody UsuarioSistemaDTO usuarioDTO) {
        UsuarioSistemaDTO usuarioActualizado = usuarioService.update(id, usuarioDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @PatchMapping("/{id}/toggle-estado")
    public ResponseEntity<Void> toggleEstadoUsuario(@PathVariable Long id) {
        usuarioService.toggleEstado(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}