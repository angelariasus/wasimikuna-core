package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstitucionEducativaDTO {
    private Long institucionId;
    private UsuarioSistemaDTO usuario;
    private String codigoModular;
    private String anexo;
    private String nombre;
    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
    private String ubigeo;
    private LocalDate fechaRegistro;
    private Integer estadoActivo;
}