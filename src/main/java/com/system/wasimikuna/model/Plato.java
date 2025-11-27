package com.system.wasimikuna.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "Plato")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plato {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plato_id")
    private Long platoId;
    
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;
    
    @Column(name = "aporte_calorico", precision = 6, scale = 2)
    private BigDecimal aporteCalorico;
    
    @Column(name = "aporte_proteico", precision = 6, scale = 2)
    private BigDecimal aporteProteico;
    
    @Column(name = "aporte_hierro", precision = 6, scale = 2)
    private BigDecimal aporteHierro;
    
    @Column(name = "region_origen", length = 50)
    private String regionOrigen;
    
    @Lob
    @Column(name = "receta_texto")
    private String recetaTexto;
}