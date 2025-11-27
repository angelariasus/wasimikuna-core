package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AfiliadoDTO {
    private Long afiliadoId;
    private UsuarioSistemaDTO usuario;
    private String tipo;
    private String ruc;
    private String razonSocial;
    private String direccion;
    private String contactoNombre;
    private String contactoTelefono;
    private Integer estado;
    private Integer calificacionSanitaria;
    private LocalDate fechaCreacion;
}