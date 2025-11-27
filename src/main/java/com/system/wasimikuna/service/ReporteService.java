package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.KardexDTO;
import com.system.wasimikuna.dto.StockDTO;
import com.system.wasimikuna.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public List<KardexDTO> getKardexEntradas() {
        return reporteRepository.getKardexEntradas();
    }

    public List<StockDTO> getStockActual() {
        return reporteRepository.getStockActual();
    }

    public List<StockDTO> getStockByInstitucion(Long institucionId) {
        return reporteRepository.getStockByInstitucion(institucionId);
    }

    public List<KardexDTO> getProductosProximosAVencer(int diasAlerta) {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().plusDays(diasAlerta);
        return reporteRepository.getProductosProximosAVencer(fechaInicio, fechaFin);
    }

    public List<KardexDTO> getProductosVencidosHoy() {
        LocalDate hoy = LocalDate.now();
        return reporteRepository.getProductosProximosAVencer(hoy, hoy);
    }

    public List<KardexDTO> getProductosVencidosRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return reporteRepository.getProductosProximosAVencer(fechaInicio, fechaFin);
    }
}