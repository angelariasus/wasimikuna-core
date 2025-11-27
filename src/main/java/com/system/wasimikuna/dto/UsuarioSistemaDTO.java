package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSistemaDTO {
    private Long usuarioId;
    private String username;
    private String email;
    private RolDTO rol;
    private Integer estado;
    private String fotoMimeType;
    private String fotoNombre;
    private Timestamp ultimoAcceso;
    private Integer intentosFallidos;
    private LocalDate fechaCreacion;
    
    // Constructor sin campos sensibles (password, foto)
    public UsuarioSistemaDTO(Long usuarioId, String username, String email, RolDTO rol, Integer estado, LocalDate fechaCreacion) {
        this.usuarioId = usuarioId;
        this.username = username;
        this.email = email;
        this.rol = rol;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }
}