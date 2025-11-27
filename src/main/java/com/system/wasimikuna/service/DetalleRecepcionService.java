package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.DetalleRecepcionDTO;
import com.system.wasimikuna.dto.ProductoDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.DetalleRecepcion;
import com.system.wasimikuna.model.Recepcion;
import com.system.wasimikuna.model.Producto;
import com.system.wasimikuna.repository.DetalleRecepcionRepository;
import com.system.wasimikuna.repository.RecepcionRepository;
import com.system.wasimikuna.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DetalleRecepcionService {

    private final DetalleRecepcionRepository detalleRepository;
    private final RecepcionRepository recepcionRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<DetalleRecepcionDTO> findAll() {
        return detalleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DetalleRecepcionDTO findById(Long id) {
        DetalleRecepcion detalle = detalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle Recepción", "id", id));
        return convertToDTO(detalle);
    }

    @Transactional(readOnly = true)
    public List<DetalleRecepcionDTO> findByRecepcion(Long recepcionId) {
        return detalleRepository.findByRecepcionRecepcionId(recepcionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleRecepcionDTO> findByProducto(Long productoId) {
        return detalleRepository.findByProductoProductoId(productoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleRecepcionDTO> findConRechazos() {
        return detalleRepository.findRejectedItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleRecepcionDTO> findByLote(String lote) {
        return detalleRepository.findByLoteFabricacion(lote).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleRecepcionDTO> findProximosAVencer(Integer dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return detalleRepository.findExpiringSoon(fechaLimite).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DetalleRecepcionDTO save(DetalleRecepcionDTO detalleDTO) {
        // Validar que la recepción existe
        Recepcion recepcion = recepcionRepository.findById(detalleDTO.getRecepcionId())
                .orElseThrow(() -> new ResourceNotFoundException("Recepción", "id", detalleDTO.getRecepcionId()));

        // Validar que el producto existe
        Producto producto = productoRepository.findById(detalleDTO.getProducto().getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", detalleDTO.getProducto().getProductoId()));

        // Validar datos básicos
        validarDatos(detalleDTO);

        DetalleRecepcion detalle = convertToEntity(detalleDTO, recepcion, producto);
        DetalleRecepcion savedDetalle = detalleRepository.save(detalle);
        return convertToDTO(savedDetalle);
    }

    public DetalleRecepcionDTO update(Long id, DetalleRecepcionDTO detalleDTO) {
        DetalleRecepcion existingDetalle = detalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle Recepción", "id", id));

        // Validar que la recepción no esté completada/rechazada
        if (existingDetalle.getRecepcion().getEstadoConformidad() == Recepcion.EstadoConformidad.RECHAZADO ||
            existingDetalle.getRecepcion().getEstadoConformidad() == Recepcion.EstadoConformidad.CONFORME) {
            throw new BusinessLogicException("No se puede modificar el detalle de una recepción finalizada");
        }

        // Validar datos
        validarDatos(detalleDTO);

        existingDetalle.setLoteFabricacion(detalleDTO.getLoteFabricacion());
        existingDetalle.setFechaVencimiento(detalleDTO.getFechaVencimiento());
        existingDetalle.setCantidadRecibida(detalleDTO.getCantidadRecibida());
        existingDetalle.setCantidadRechazada(detalleDTO.getCantidadRechazada());
        existingDetalle.setMotivoRechazo(detalleDTO.getMotivoRechazo());

        DetalleRecepcion updatedDetalle = detalleRepository.save(existingDetalle);
        return convertToDTO(updatedDetalle);
    }

    public DetalleRecepcionDTO rechazarCantidad(Long id, Integer cantidadRechazada, String motivoRechazo) {
        DetalleRecepcion detalle = detalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle Recepción", "id", id));

        if (cantidadRechazada < 0) {
            throw new BusinessLogicException("La cantidad rechazada no puede ser negativa");
        }

        if (cantidadRechazada > detalle.getCantidadRecibida()) {
            throw new BusinessLogicException("La cantidad rechazada no puede ser mayor a la recibida");
        }

        if (motivoRechazo == null || motivoRechazo.trim().isEmpty()) {
            throw new BusinessLogicException("El motivo de rechazo es obligatorio");
        }

        detalle.setCantidadRechazada(cantidadRechazada);
        detalle.setMotivoRechazo(motivoRechazo);

        DetalleRecepcion updatedDetalle = detalleRepository.save(detalle);
        return convertToDTO(updatedDetalle);
    }

    public void deleteById(Long id) {
        DetalleRecepcion detalle = detalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle Recepción", "id", id));

        // Solo permitir eliminación si la recepción no está finalizada
        if (detalle.getRecepcion().getEstadoConformidad() == Recepcion.EstadoConformidad.CONFORME ||
            detalle.getRecepcion().getEstadoConformidad() == Recepcion.EstadoConformidad.RECHAZADO) {
            throw new BusinessLogicException("No se pueden eliminar detalles de una recepción finalizada");
        }

        detalleRepository.deleteById(id);
    }

    public void deleteByRecepcionId(Long recepcionId) {
        List<DetalleRecepcion> detalles = detalleRepository.findByRecepcionRecepcionId(recepcionId);
        for (DetalleRecepcion detalle : detalles) {
            deleteById(detalle.getDetRecepcionId());
        }
    }

    private void validarDatos(DetalleRecepcionDTO detalleDTO) {
        if (detalleDTO.getLoteFabricacion() == null || detalleDTO.getLoteFabricacion().trim().isEmpty()) {
            throw new BusinessLogicException("El lote de fabricación es obligatorio");
        }

        if (detalleDTO.getFechaVencimiento() == null) {
            throw new BusinessLogicException("La fecha de vencimiento es obligatoria");
        }

        if (detalleDTO.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new BusinessLogicException("La fecha de vencimiento no puede ser anterior a hoy");
        }

        if (detalleDTO.getCantidadRecibida() == null || detalleDTO.getCantidadRecibida() < 0) {
            throw new BusinessLogicException("La cantidad recibida no puede ser negativa");
        }

        if (detalleDTO.getCantidadRechazada() != null && detalleDTO.getCantidadRechazada() < 0) {
            throw new BusinessLogicException("La cantidad rechazada no puede ser negativa");
        }
    }

    private DetalleRecepcionDTO convertToDTO(DetalleRecepcion detalle) {
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setProductoId(detalle.getProducto().getProductoId());
        productoDTO.setNombre(detalle.getProducto().getNombre());
        productoDTO.setUnidadMedida(detalle.getProducto().getUnidadMedida());

        return new DetalleRecepcionDTO(
                detalle.getDetRecepcionId(),
                detalle.getRecepcion().getRecepcionId(),
                productoDTO,
                detalle.getLoteFabricacion(),
                detalle.getFechaVencimiento(),
                detalle.getCantidadRecibida(),
                detalle.getCantidadRechazada(),
                detalle.getMotivoRechazo(),
                detalle.getEvidenciaMimeType(),
                detalle.getEvidenciaNombreArchivo()
        );
    }

    private DetalleRecepcion convertToEntity(DetalleRecepcionDTO dto, Recepcion recepcion, Producto producto) {
        DetalleRecepcion detalle = new DetalleRecepcion();
        detalle.setDetRecepcionId(dto.getDetRecepcionId());
        detalle.setRecepcion(recepcion);
        detalle.setProducto(producto);
        detalle.setLoteFabricacion(dto.getLoteFabricacion());
        detalle.setFechaVencimiento(dto.getFechaVencimiento());
        detalle.setCantidadRecibida(dto.getCantidadRecibida());
        detalle.setCantidadRechazada(dto.getCantidadRechazada());
        detalle.setMotivoRechazo(dto.getMotivoRechazo());
        detalle.setEvidenciaMimeType(dto.getEvidenciaMimeType());
        detalle.setEvidenciaNombreArchivo(dto.getEvidenciaNombreArchivo());
        return detalle;
    }
}