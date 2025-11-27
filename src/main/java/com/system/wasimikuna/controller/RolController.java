package com.system.wasimikuna.controller;

import com.system.wasimikuna.dto.RolDTO;
import com.system.wasimikuna.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RolController {

    private final RolService rolService;

    @GetMapping
    public ResponseEntity<List<RolDTO>> getAllRoles() {
        List<RolDTO> roles = rolService.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> getRolById(@PathVariable Integer id) {
        RolDTO rol = rolService.findById(id);
        return ResponseEntity.ok(rol);
    }

    @PostMapping
    public ResponseEntity<RolDTO> createRol(@RequestBody RolDTO rolDTO) {
        RolDTO nuevoRol = rolService.save(rolDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> updateRol(@PathVariable Integer id, @RequestBody RolDTO rolDTO) {
        RolDTO rolActualizado = rolService.update(id, rolDTO);
        return ResponseEntity.ok(rolActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Integer id) {
        rolService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}