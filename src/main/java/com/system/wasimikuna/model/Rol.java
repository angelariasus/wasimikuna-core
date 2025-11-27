package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer rolId;
    
    @Column(name = "nombre", length = 30, unique = true, nullable = false)
    private String nombre;
    
    @Column(name = "descripcion", length = 150)
    private String descripcion;
    
    @Column(name = "nivel_acceso")
    private Integer nivelAcceso = 1;
}