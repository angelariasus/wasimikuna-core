package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.IncidenciaSanitariaDTO;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.IncidenciaSanitaria;
import com.system.wasimikuna.model.IncidenciaSanitaria.TipoRiesgo;
import com.system.wasimikuna.model.IncidenciaSanitaria.EstadoAtencion;
import com.system.wasimikuna.repository.IncidenciaSanitariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IncidenciaSanitariaService {

    private final IncidenciaSanitariaRepository incidenciaRepository;

    @Transactional(readOnly = true)
    public List<IncidenciaSanitariaDTO> findAll() {
        return incidenciaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IncidenciaSanitariaDTO findById(Long id) {
        IncidenciaSanitaria incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia Sanitaria", "id", id));
        return convertToDTO(incidencia);
    }

    @Transactional(readOnly = true)
    public List<IncidenciaSanitariaDTO> findByInstitucion(Long institucionId) {
        return incidenciaRepository.findByInstitucionInstitucionId(institucionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidenciaSanitariaDTO> findByProducto(Long productoId) {
        return incidenciaRepository.findByProductoProductoId(productoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidenciaSanitariaDTO> findByTipoRiesgo(String tipoRiesgo) {
        TipoRiesgo tipo = TipoRiesgo.valueOf(tipoRiesgo.toUpperCase());
        return incidenciaRepository.findByTipoRiesgo(tipo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidenciaSanitariaDTO> findByEstadoAtencion(String estadoAtencion) {
        EstadoAtencion estado = EstadoAtencion.valueOf(estadoAtencion.toUpperCase());
        return incidenciaRepository.findByEstadoAtencion(estado).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidenciaSanitariaDTO> findIncidenciasPendientes() {
        return incidenciaRepository.findPendingIncidents().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidenciaSanitariaDTO> findIncidenciasCriticas() {
        return incidenciaRepository.findCriticalIncidents().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidenciaSanitariaDTO> findByLoteAfectado(String lote) {
        return incidenciaRepository.findByLoteAfectado(lote).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidenciaSanitariaDTO> findByFechaRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Timestamp tsInicio = Timestamp.valueOf(fechaInicio);
        Timestamp tsFin = Timestamp.valueOf(fechaFin);
        return incidenciaRepository.findByFechaReporteBetween(tsInicio, tsFin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public IncidenciaSanitariaDTO save(IncidenciaSanitariaDTO incidenciaDTO) {
        IncidenciaSanitaria incidencia = convertToEntity(incidenciaDTO);
        incidencia.setEstadoAtencion(EstadoAtencion.REPORTADO);
        IncidenciaSanitaria savedIncidencia = incidenciaRepository.save(incidencia);
        return convertToDTO(savedIncidencia);
    }

    public IncidenciaSanitariaDTO update(Long id, IncidenciaSanitariaDTO incidenciaDTO) {
        IncidenciaSanitaria existingIncidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia Sanitaria", "id", id));

        existingIncidencia.setTipoRiesgo(incidenciaDTO.getTipoRiesgo() != null ? 
            TipoRiesgo.valueOf(incidenciaDTO.getTipoRiesgo().toUpperCase()) : null);
        existingIncidencia.setDescripcionDetallada(incidenciaDTO.getDescripcionDetallada());
        existingIncidencia.setLoteAfectado(incidenciaDTO.getLoteAfectado());
        existingIncidencia.setAccionTomada(incidenciaDTO.getAccionTomada());

        IncidenciaSanitaria updatedIncidencia = incidenciaRepository.save(existingIncidencia);
        return convertToDTO(updatedIncidencia);
    }

    public IncidenciaSanitariaDTO cambiarEstadoAtencion(Long id, String nuevoEstado, String accionTomada) {
        IncidenciaSanitaria incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia Sanitaria", "id", id));

        EstadoAtencion estado = EstadoAtencion.valueOf(nuevoEstado.toUpperCase());
        incidencia.setEstadoAtencion(estado);
        
        if (accionTomada != null && !accionTomada.trim().isEmpty()) {
            incidencia.setAccionTomada(accionTomada);
        }

        IncidenciaSanitaria updatedIncidencia = incidenciaRepository.save(incidencia);
        return convertToDTO(updatedIncidencia);
    }

    public void deleteById(Long id) {
        if (!incidenciaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Incidencia Sanitaria", "id", id);
        }
        incidenciaRepository.deleteById(id);
    }

    private IncidenciaSanitariaDTO convertToDTO(IncidenciaSanitaria incidencia) {
        return new IncidenciaSanitariaDTO(
                incidencia.getIncidenciaId(),
                null, // InstitucionEducativaDTO - se puede cargar por separado si es necesario
                null, // ProductoDTO - se puede cargar por separado si es necesario
                incidencia.getLoteAfectado(),
                incidencia.getTipoRiesgo() != null ? incidencia.getTipoRiesgo().name() : null,
                incidencia.getDescripcionDetallada(),
                incidencia.getFechaReporte(),
                incidencia.getEstadoAtencion() != null ? incidencia.getEstadoAtencion().name() : null,
                incidencia.getAccionTomada(),
                null, // ComiteGestionDTO - se puede cargar por separado si es necesario
                null  // UsuarioSistemaDTO - se puede cargar por separado si es necesario
        );
    }

    private IncidenciaSanitaria convertToEntity(IncidenciaSanitariaDTO dto) {
        IncidenciaSanitaria incidencia = new IncidenciaSanitaria();
        incidencia.setIncidenciaId(dto.getIncidenciaId());
        incidencia.setLoteAfectado(dto.getLoteAfectado());
        incidencia.setTipoRiesgo(dto.getTipoRiesgo() != null ? 
            TipoRiesgo.valueOf(dto.getTipoRiesgo().toUpperCase()) : null);
        incidencia.setDescripcionDetallada(dto.getDescripcionDetallada());
        incidencia.setFechaReporte(dto.getFechaReporte());
        incidencia.setEstadoAtencion(dto.getEstadoAtencion() != null ? 
            EstadoAtencion.valueOf(dto.getEstadoAtencion().toUpperCase()) : null);
        incidencia.setAccionTomada(dto.getAccionTomada());
        return incidencia;
    }
}