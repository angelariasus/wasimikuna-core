package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.OrdenCompraDTO;
import com.system.wasimikuna.dto.DetalleOrdenDTO;
import com.system.wasimikuna.dto.AfiliadoDTO;
import com.system.wasimikuna.dto.UsuarioSistemaDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.OrdenCompra;
import com.system.wasimikuna.model.DetalleOrden;
import com.system.wasimikuna.model.Afiliado;
import com.system.wasimikuna.model.UsuarioSistema;
import com.system.wasimikuna.repository.OrdenCompraRepository;
import com.system.wasimikuna.repository.DetalleOrdenRepository;
import com.system.wasimikuna.repository.AfiliadoRepository;
import com.system.wasimikuna.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final DetalleOrdenRepository detalleOrdenRepository;
    private final AfiliadoRepository afiliadoRepository;
    private final UsuarioSistemaRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> findAll() {
        return ordenCompraRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrdenCompraDTO findById(Long id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de Compra", "id", id));
        return convertToDTO(orden);
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> findByAfiliado(Long afiliadoId) {
        return ordenCompraRepository.findByAfiliadoAfiliadoId(afiliadoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> findByEstado(Integer estado) {
        return ordenCompraRepository.findByEstado(estado).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> findByFechaRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return ordenCompraRepository.findByFechaEmisionBetween(fechaInicio, fechaFin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> findOrdenesVencidas() {
        return ordenCompraRepository.findOverdueOrders(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrdenCompraDTO save(OrdenCompraDTO ordenDTO) {
        // Validar que el afiliado existe y está activo
        Afiliado afiliado = afiliadoRepository.findById(ordenDTO.getAfiliado().getAfiliadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Afiliado", "id", ordenDTO.getAfiliado().getAfiliadoId()));
        
        if (afiliado.getEstado() == 0) {
            throw new BusinessLogicException("No se puede crear orden para afiliado inactivo");
        }

        // Validar usuario creador
        UsuarioSistema usuario = null;
        if (ordenDTO.getUsuarioCreacion() != null) {
            usuario = usuarioRepository.findById(ordenDTO.getUsuarioCreacion().getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", ordenDTO.getUsuarioCreacion().getUsuarioId()));
        }

        OrdenCompra orden = new OrdenCompra();
        orden.setAfiliado(afiliado);
        orden.setUsuarioCreacion(usuario);
        orden.setFechaEntregaPrevista(ordenDTO.getFechaEntregaPrevista());
        orden.setEstado(0); // PENDIENTE

        // Calcular total basado en detalles
        BigDecimal total = BigDecimal.ZERO;
        if (ordenDTO.getDetalles() != null && !ordenDTO.getDetalles().isEmpty()) {
            for (DetalleOrdenDTO detalle : ordenDTO.getDetalles()) {
                BigDecimal subtotal = detalle.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad()));
                total = total.add(subtotal);
            }
        }
        orden.setTotal(total);

        OrdenCompra savedOrden = ordenCompraRepository.save(orden);
        return convertToDTO(savedOrden);
    }

    public OrdenCompraDTO update(Long id, OrdenCompraDTO ordenDTO) {
        OrdenCompra existingOrden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de Compra", "id", id));

        // Solo permitir actualización si está en estado PENDIENTE
        if (existingOrden.getEstado() > 1) {
            throw new BusinessLogicException("No se puede modificar una orden que ya está en proceso");
        }

        existingOrden.setFechaEntregaPrevista(ordenDTO.getFechaEntregaPrevista());
        existingOrden.setTotal(ordenDTO.getTotal());
        existingOrden.setFechaModificacion(LocalDate.now());

        OrdenCompra updatedOrden = ordenCompraRepository.save(existingOrden);
        return convertToDTO(updatedOrden);
    }

    public OrdenCompraDTO cambiarEstado(Long id, Integer nuevoEstado) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de Compra", "id", id));

        // Validar transiciones de estado válidas
        if (!esTransicionValida(orden.getEstado(), nuevoEstado)) {
            throw new BusinessLogicException("Transición de estado inválida");
        }

        orden.setEstado(nuevoEstado);
        orden.setFechaModificacion(LocalDate.now());

        OrdenCompra updatedOrden = ordenCompraRepository.save(orden);
        return convertToDTO(updatedOrden);
    }

    public void deleteById(Long id) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de Compra", "id", id));

        // Solo permitir eliminación si está en estado PENDIENTE
        if (orden.getEstado() > 0) {
            throw new BusinessLogicException("No se puede eliminar una orden que ya está procesándose");
        }

        ordenCompraRepository.deleteById(id);
    }

    private boolean esTransicionValida(Integer estadoActual, Integer nuevoEstado) {
        // 0=PENDIENTE, 1=APROBADA, 2=EN_PROCESO, 3=COMPLETADA
        return (estadoActual == 0 && nuevoEstado == 1) ||  // PENDIENTE -> APROBADA
               (estadoActual == 1 && nuevoEstado == 2) ||  // APROBADA -> EN_PROCESO
               (estadoActual == 2 && nuevoEstado == 3) ||  // EN_PROCESO -> COMPLETADA
               (estadoActual < 2 && nuevoEstado == 0);     // Volver a PENDIENTE si no está muy avanzada
    }

    private OrdenCompraDTO convertToDTO(OrdenCompra orden) {
        // Conversión simplificada - se puede expandir más tarde
        AfiliadoDTO afiliadoDTO = new AfiliadoDTO();
        afiliadoDTO.setAfiliadoId(orden.getAfiliado().getAfiliadoId());
        afiliadoDTO.setRazonSocial(orden.getAfiliado().getRazonSocial());
        
        UsuarioSistemaDTO usuarioDTO = null;
        if (orden.getUsuarioCreacion() != null) {
            usuarioDTO = new UsuarioSistemaDTO();
            usuarioDTO.setUsuarioId(orden.getUsuarioCreacion().getUsuarioId());
            usuarioDTO.setUsername(orden.getUsuarioCreacion().getUsername());
        }

        // Cargar detalles
        List<DetalleOrdenDTO> detalles = detalleOrdenRepository.findByOrdenCompraOrdenCompraId(orden.getOrdenCompraId())
                .stream()
                .map(this::convertDetalleToDTO)
                .collect(Collectors.toList());

        return new OrdenCompraDTO(
                orden.getOrdenCompraId(),
                afiliadoDTO,
                orden.getFechaEmision(),
                orden.getEstado(),
                orden.getTotal(),
                orden.getFechaEntregaPrevista(),
                usuarioDTO,
                orden.getFechaModificacion(),
                detalles
        );
    }

    private DetalleOrdenDTO convertDetalleToDTO(DetalleOrden detalle) {
        return new DetalleOrdenDTO(
                detalle.getDetalleId(),
                detalle.getOrdenCompra().getOrdenCompraId(),
                null, // ProductoDTO se cargaría desde ProductoService si fuera necesario
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()
        );
    }
}