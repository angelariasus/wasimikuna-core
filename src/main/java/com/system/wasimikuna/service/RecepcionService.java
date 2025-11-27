package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.RecepcionDTO;
import com.system.wasimikuna.dto.EnvioDTO;
import com.system.wasimikuna.dto.ComiteGestionDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.Recepcion;
import com.system.wasimikuna.model.Recepcion.EstadoConformidad;
import com.system.wasimikuna.model.Envio;
import com.system.wasimikuna.model.ComiteGestion;
import com.system.wasimikuna.repository.RecepcionRepository;
import com.system.wasimikuna.repository.EnvioRepository;
import com.system.wasimikuna.repository.ComiteGestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecepcionService {

    private final RecepcionRepository recepcionRepository;
    private final EnvioRepository envioRepository;
    private final ComiteGestionRepository comiteRepository;

    @Transactional(readOnly = true)
    public List<RecepcionDTO> findAll() {
        return recepcionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecepcionDTO findById(Long id) {
        Recepcion recepcion = recepcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepción", "id", id));
        return convertToDTO(recepcion);
    }

    @Transactional(readOnly = true)
    public List<RecepcionDTO> findByEnvio(Long envioId) {
        return recepcionRepository.findByEnvioEnvioId(envioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecepcionDTO> findByComiteMiembro(Long miembroId) {
        return recepcionRepository.findByComiteMiembroMiembroId(miembroId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecepcionDTO> findByEstado(String estado) {
        EstadoConformidad estadoEnum = EstadoConformidad.valueOf(estado.toUpperCase());
        return recepcionRepository.findByEstadoConformidad(estadoEnum).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecepcionDTO> findRecepcionesConProblemas() {
        return recepcionRepository.findProblematicReceptions().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecepcionDTO> findConObservaciones() {
        return recepcionRepository.findWithObservations().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecepcionDTO> findByInstitucion(Long institucionId) {
        return recepcionRepository.findByInstitucionId(institucionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecepcionDTO> findByFechaRango(Timestamp fechaInicio, Timestamp fechaFin) {
        return recepcionRepository.findByFechaRecepcionBetween(fechaInicio, fechaFin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecepcionDTO> findByInstitucionYEstado(Long institucionId, String estado) {
        EstadoConformidad estadoEnum = EstadoConformidad.valueOf(estado.toUpperCase());
        return recepcionRepository.findByInstitucionAndEstado(institucionId, estadoEnum).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RecepcionDTO save(RecepcionDTO recepcionDTO) {
        // Validar que el envío existe
        Envio envio = envioRepository.findById(recepcionDTO.getEnvio().getEnvioId())
                .orElseThrow(() -> new ResourceNotFoundException("Envío", "id", recepcionDTO.getEnvio().getEnvioId()));

        // Validar comité miembro si se proporciona
        ComiteGestion comiteMiembro = null;
        if (recepcionDTO.getComiteMiembro() != null && recepcionDTO.getComiteMiembro().getMiembroId() != null) {
            comiteMiembro = comiteRepository.findById(recepcionDTO.getComiteMiembro().getMiembroId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comité Miembro", "id", recepcionDTO.getComiteMiembro().getMiembroId()));
        }

        Recepcion recepcion = convertToEntity(recepcionDTO, envio, comiteMiembro);
        Recepcion savedRecepcion = recepcionRepository.save(recepcion);

        return convertToDTO(savedRecepcion);
    }

    public RecepcionDTO update(Long id, RecepcionDTO recepcionDTO) {
        Recepcion existingRecepcion = recepcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepción", "id", id));

        // Solo permitir actualización si no está rechazado (estado final)
        if (existingRecepcion.getEstadoConformidad() == EstadoConformidad.RECHAZADO) {
            throw new BusinessLogicException("No se puede modificar una recepción rechazada");
        }

        existingRecepcion.setObservacionesGenerales(recepcionDTO.getObservacionesGenerales());

        // Actualizar comité miembro si se proporciona
        if (recepcionDTO.getComiteMiembro() != null && recepcionDTO.getComiteMiembro().getMiembroId() != null) {
            ComiteGestion comiteMiembro = comiteRepository.findById(recepcionDTO.getComiteMiembro().getMiembroId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comité Miembro", "id", recepcionDTO.getComiteMiembro().getMiembroId()));
            existingRecepcion.setComiteMiembro(comiteMiembro);
        }

        Recepcion updatedRecepcion = recepcionRepository.save(existingRecepcion);
        return convertToDTO(updatedRecepcion);
    }

    public RecepcionDTO cambiarEstado(Long id, String nuevoEstado) {
        Recepcion recepcion = recepcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepción", "id", id));

        EstadoConformidad estadoEnum = EstadoConformidad.valueOf(nuevoEstado.toUpperCase());

        // Validar que no sea un estado final que no permita cambios
        if (recepcion.getEstadoConformidad() == EstadoConformidad.RECHAZADO) {
            throw new BusinessLogicException("No se puede cambiar el estado de una recepción rechazada");
        }

        recepcion.setEstadoConformidad(estadoEnum);

        Recepcion updatedRecepcion = recepcionRepository.save(recepcion);
        return convertToDTO(updatedRecepcion);
    }

    public RecepcionDTO marcarComoConforme(Long id, String observaciones) {
        Recepcion recepcion = recepcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepción", "id", id));

        recepcion.setEstadoConformidad(EstadoConformidad.CONFORME);
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            recepcion.setObservacionesGenerales(observaciones);
        }

        Recepcion updatedRecepcion = recepcionRepository.save(recepcion);
        return convertToDTO(updatedRecepcion);
    }

    public RecepcionDTO marcarComoObservado(Long id, String observaciones) {
        Recepcion recepcion = recepcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepción", "id", id));

        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new BusinessLogicException("Las observaciones son obligatorias para marcar como observado");
        }

        recepcion.setEstadoConformidad(EstadoConformidad.OBSERVADO);
        recepcion.setObservacionesGenerales(observaciones);

        Recepcion updatedRecepcion = recepcionRepository.save(recepcion);
        return convertToDTO(updatedRecepcion);
    }

    public RecepcionDTO rechazarRecepcion(Long id, String motivoRechazo) {
        Recepcion recepcion = recepcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepción", "id", id));

        if (motivoRechazo == null || motivoRechazo.trim().isEmpty()) {
            throw new BusinessLogicException("El motivo de rechazo es obligatorio");
        }

        recepcion.setEstadoConformidad(EstadoConformidad.RECHAZADO);
        recepcion.setObservacionesGenerales(motivoRechazo);

        Recepcion updatedRecepcion = recepcionRepository.save(recepcion);
        return convertToDTO(updatedRecepcion);
    }

    public void deleteById(Long id) {
        Recepcion recepcion = recepcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepción", "id", id));

        // Solo permitir eliminación si no está en estado final
        if (recepcion.getEstadoConformidad() == EstadoConformidad.CONFORME || 
            recepcion.getEstadoConformidad() == EstadoConformidad.RECHAZADO) {
            throw new BusinessLogicException("No se pueden eliminar recepciones en estado final");
        }

        recepcionRepository.deleteById(id);
    }

    private RecepcionDTO convertToDTO(Recepcion recepcion) {
        // Simplificar la conversión para evitar dependencias circulares
        EnvioDTO envioDTO = new EnvioDTO();
        envioDTO.setEnvioId(recepcion.getEnvio().getEnvioId());
        envioDTO.setConductorNombre(recepcion.getEnvio().getConductorNombre());
        envioDTO.setPlacaVehiculo(recepcion.getEnvio().getPlacaVehiculo());

        ComiteGestionDTO comiteDTO = null;
        if (recepcion.getComiteMiembro() != null) {
            comiteDTO = new ComiteGestionDTO();
            comiteDTO.setMiembroId(recepcion.getComiteMiembro().getMiembroId());
            comiteDTO.setNombreCompleto(recepcion.getComiteMiembro().getNombreCompleto());
            comiteDTO.setDni(recepcion.getComiteMiembro().getDni());
            comiteDTO.setCargo(recepcion.getComiteMiembro().getCargo() != null ? recepcion.getComiteMiembro().getCargo().name() : null);
        }

        return new RecepcionDTO(
                recepcion.getRecepcionId(),
                envioDTO,
                comiteDTO,
                recepcion.getFechaRecepcion(),
                recepcion.getEstadoConformidad() != null ? recepcion.getEstadoConformidad().name() : null,
                recepcion.getObservacionesGenerales(),
                recepcion.getActaMimeType(),
                recepcion.getActaNombreArchivo(),
                null // detalles se cargarán por separado
        );
    }

    private Recepcion convertToEntity(RecepcionDTO dto, Envio envio, ComiteGestion comiteMiembro) {
        Recepcion recepcion = new Recepcion();
        recepcion.setRecepcionId(dto.getRecepcionId());
        recepcion.setEnvio(envio);
        recepcion.setComiteMiembro(comiteMiembro);
        recepcion.setFechaRecepcion(dto.getFechaRecepcion() != null ? dto.getFechaRecepcion() : new Timestamp(System.currentTimeMillis()));
        recepcion.setEstadoConformidad(dto.getEstadoConformidad() != null ? EstadoConformidad.valueOf(dto.getEstadoConformidad().toUpperCase()) : EstadoConformidad.CONFORME);
        recepcion.setObservacionesGenerales(dto.getObservacionesGenerales());
        recepcion.setActaMimeType(dto.getActaMimeType());
        recepcion.setActaNombreArchivo(dto.getActaNombreArchivo());
        return recepcion;
    }
}
