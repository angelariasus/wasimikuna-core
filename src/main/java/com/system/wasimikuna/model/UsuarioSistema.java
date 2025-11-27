package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Blob;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "Usuario_Sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSistema {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuarioId;
    
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;
    
    @Column(name = "password_hash", length = 64, nullable = false)
    private String passwordHash;
    
    @Column(name = "email", length = 100, unique = true)
    private String email;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
    
    @Column(name = "estado")
    private Integer estado = 1;
    
    @Lob
    @Column(name = "foto_perfil")
    private Blob fotoPerfil;
    
    @Column(name = "foto_mime_type", length = 50)
    private String fotoMimeType;
    
    @Column(name = "foto_nombre", length = 100)
    private String fotoNombre;
    
    @Column(name = "ultimo_acceso")
    private Timestamp ultimoAcceso;
    
    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;
    
    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDate.now();
    }
}