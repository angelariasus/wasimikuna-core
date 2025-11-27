package com.system.wasimikuna.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private boolean success;
    private String message;
    private UsuarioSistemaDTO usuario;
    private String token; // Para JWT si se implementa
    
    // Constructor para respuestas exitosas
    public LoginResponseDTO(boolean success, UsuarioSistemaDTO usuario) {
        this.success = success;
        this.usuario = usuario;
        this.message = success ? "Login exitoso" : "Credenciales inválidas";
    }
    
    // Constructor para respuestas con error
    public LoginResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}