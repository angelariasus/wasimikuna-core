package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearUsuarioDTO {
    private String username;
    private String password;
    private String email;
    private Integer rolId;
    
    // Campos específicos según el tipo de usuario
    // Para Institución Educativa
    private String codigoModular;
    private String anexo;
    private String nombre;
    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
    private String ubigeo;
    
    // Para Afiliado
    private String tipo;
    private String ruc;
    private String razonSocial;
    private String contactoNombre;
    private String contactoTelefono;
}