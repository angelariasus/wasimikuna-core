package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.DetalleOrdenDTO;
import com.system.wasimikuna.dto.ProductoDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.DetalleOrden;
import com.system.wasimikuna.model.OrdenCompra;
import com.system.wasimikuna.model.Producto;
import com.system.wasimikuna.repository.DetalleOrdenRepository;
import com.system.wasimikuna.repository.OrdenCompraRepository;
import com.system.wasimikuna.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DetalleOrdenService {

    private final DetalleOrdenRepository detalleOrdenRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<DetalleOrdenDTO> findAll() {
        return detalleOrdenRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DetalleOrdenDTO findById(Long id) {
        DetalleOrden detalle = detalleOrdenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle Orden", "id", id));
        return convertToDTO(detalle);
    }

    @Transactional(readOnly = true)
    public List<DetalleOrdenDTO> findByOrdenCompra(Long ordenCompraId) {
        return detalleOrdenRepository.findByOrdenCompraOrdenCompraId(ordenCompraId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleOrdenDTO> findByProducto(Long productoId) {
        return detalleOrdenRepository.findByProductoProductoId(productoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleOrdenDTO> findByCantidadMinima(Integer cantidadMinima) {
        return detalleOrdenRepository.findByCantidadGreaterThanEqual(cantidadMinima).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetalleOrdenDTO> findByMontoMinimo(BigDecimal montoMinimo) {
        return detalleOrdenRepository.findBySubtotalGreaterThanEqual(montoMinimo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalOrden(Long ordenCompraId) {
        BigDecimal total = detalleOrdenRepository.calculateTotalByOrdenCompra(ordenCompraId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<DetalleOrdenDTO> findByProductoYRangoFechas(Long productoId, LocalDate fechaInicio, LocalDate fechaFin) {
        return detalleOrdenRepository.findByProductoAndFechaRange(productoId, fechaInicio, fechaFin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DetalleOrdenDTO save(DetalleOrdenDTO detalleDTO) {
        // Validar que la orden de compra existe
        OrdenCompra orden = ordenCompraRepository.findById(detalleDTO.getOrdenCompraId())
                .orElseThrow(() -> new ResourceNotFoundException("Orden Compra", "id", detalleDTO.getOrdenCompraId()));

        // Validar que el producto existe
        Producto producto = productoRepository.findById(detalleDTO.getProducto().getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", detalleDTO.getProducto().getProductoId()));

        // Validar datos del detalle
        validarDetalleOrden(detalleDTO);

        // Verificar que la orden esté en estado PENDIENTE (0) para poder modificar
        if (orden.getEstado() == null || !orden.getEstado().equals(0)) {
            throw new BusinessLogicException("Solo se pueden agregar detalles a órdenes en estado PENDIENTE");
        }

        // Verificar que no exista ya este producto en la orden
        List<DetalleOrden> detallesExistentes = detalleOrdenRepository.findByOrdenCompraOrdenCompraId(orden.getOrdenCompraId());
        boolean productoYaExiste = detallesExistentes.stream()
                .anyMatch(d -> d.getProducto().getProductoId().equals(producto.getProductoId()));
        
        if (productoYaExiste) {
            throw new BusinessLogicException("El producto ya existe en esta orden de compra");
        }

        DetalleOrden detalle = convertToEntity(detalleDTO, orden, producto);
        
        // Calcular subtotal automáticamente
        BigDecimal subtotal = calcularSubtotal(detalle.getCantidad(), detalle.getPrecioUnitario());
        detalle.setSubtotal(subtotal);

        DetalleOrden savedDetalle = detalleOrdenRepository.save(detalle);
        return convertToDTO(savedDetalle);
    }

    public DetalleOrdenDTO update(Long id, DetalleOrdenDTO detalleDTO) {
        DetalleOrden existingDetalle = detalleOrdenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle Orden", "id", id));

        // Verificar que la orden esté en estado PENDIENTE (0)
        if (existingDetalle.getOrdenCompra().getEstado() == null || 
            !existingDetalle.getOrdenCompra().getEstado().equals(0)) {
            throw new BusinessLogicException("Solo se pueden modificar detalles de órdenes en estado PENDIENTE");
        }

        // Validar datos
        validarDetalleOrden(detalleDTO);

        // Verificar duplicados solo si cambió el producto
        if (!existingDetalle.getProducto().getProductoId().equals(detalleDTO.getProducto().getProductoId())) {
            List<DetalleOrden> detallesExistentes = detalleOrdenRepository.findByOrdenCompraOrdenCompraId(
                    existingDetalle.getOrdenCompra().getOrdenCompraId());
            boolean productoYaExiste = detallesExistentes.stream()
                    .anyMatch(d -> d.getProducto().getProductoId().equals(detalleDTO.getProducto().getProductoId()) 
                            && !d.getDetalleId().equals(id));
            
            if (productoYaExiste) {
                throw new BusinessLogicException("El producto ya existe en esta orden de compra");
            }
            
            // Actualizar producto si cambió
            Producto producto = productoRepository.findById(detalleDTO.getProducto().getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", detalleDTO.getProducto().getProductoId()));
            existingDetalle.setProducto(producto);
        }

        existingDetalle.setCantidad(detalleDTO.getCantidad());
        existingDetalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
        
        // Recalcular subtotal
        BigDecimal subtotal = calcularSubtotal(existingDetalle.getCantidad(), existingDetalle.getPrecioUnitario());
        existingDetalle.setSubtotal(subtotal);

        DetalleOrden updatedDetalle = detalleOrdenRepository.save(existingDetalle);
        return convertToDTO(updatedDetalle);
    }

    public DetalleOrdenDTO actualizarCantidad(Long id, Integer nuevaCantidad) {
        DetalleOrden detalle = detalleOrdenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle Orden", "id", id));

        // Verificar que la orden esté en estado PENDIENTE (0)
        if (detalle.getOrdenCompra().getEstado() == null || 
            !detalle.getOrdenCompra().getEstado().equals(0)) {
            throw new BusinessLogicException("Solo se pueden modificar detalles de órdenes en estado PENDIENTE");
        }

        if (nuevaCantidad == null || nuevaCantidad <= 0) {
            throw new BusinessLogicException("La cantidad debe ser mayor a cero");
        }

        detalle.setCantidad(nuevaCantidad);
        
        // Recalcular subtotal
        BigDecimal subtotal = calcularSubtotal(detalle.getCantidad(), detalle.getPrecioUnitario());
        detalle.setSubtotal(subtotal);

        DetalleOrden updatedDetalle = detalleOrdenRepository.save(detalle);
        return convertToDTO(updatedDetalle);
    }

    public DetalleOrdenDTO actualizarPrecio(Long id, BigDecimal nuevoPrecio) {
        DetalleOrden detalle = detalleOrdenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle Orden", "id", id));

        // Verificar que la orden esté en estado PENDIENTE (0)
        if (detalle.getOrdenCompra().getEstado() == null || 
            !detalle.getOrdenCompra().getEstado().equals(0)) {
            throw new BusinessLogicException("Solo se pueden modificar detalles de órdenes en estado PENDIENTE");
        }

        if (nuevoPrecio == null || nuevoPrecio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessLogicException("El precio debe ser mayor a cero");
        }

        detalle.setPrecioUnitario(nuevoPrecio);
        
        // Recalcular subtotal
        BigDecimal subtotal = calcularSubtotal(detalle.getCantidad(), detalle.getPrecioUnitario());
        detalle.setSubtotal(subtotal);

        DetalleOrden updatedDetalle = detalleOrdenRepository.save(detalle);
        return convertToDTO(updatedDetalle);
    }

    public void deleteById(Long id) {
        DetalleOrden detalle = detalleOrdenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle Orden", "id", id));

        // Verificar que la orden esté en estado PENDIENTE (0)
        if (detalle.getOrdenCompra().getEstado() == null || 
            !detalle.getOrdenCompra().getEstado().equals(0)) {
            throw new BusinessLogicException("Solo se pueden eliminar detalles de órdenes en estado PENDIENTE");
        }

        detalleOrdenRepository.deleteById(id);
    }

    public void deleteByOrdenCompra(Long ordenCompraId) {
        // Verificar que la orden exista y esté en estado PENDIENTE
        OrdenCompra orden = ordenCompraRepository.findById(ordenCompraId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden Compra", "id", ordenCompraId));
        
        if (orden.getEstado() == null || !orden.getEstado().equals(0)) {
            throw new BusinessLogicException("Solo se pueden eliminar detalles de órdenes en estado PENDIENTE");
        }

        List<DetalleOrden> detalles = detalleOrdenRepository.findByOrdenCompraOrdenCompraId(ordenCompraId);
        detalleOrdenRepository.deleteAll(detalles);
    }

    public List<DetalleOrdenDTO> duplicarDetallesDeOrden(Long ordenOrigenId, Long ordenDestinoId) {
        // Validar que ambas órdenes existen
        if (!ordenCompraRepository.existsById(ordenOrigenId)) {
            throw new ResourceNotFoundException("Orden Origen", "id", ordenOrigenId);
        }
        
        OrdenCompra ordenDestino = ordenCompraRepository.findById(ordenDestinoId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden Destino", "id", ordenDestinoId));

        // Verificar que la orden destino esté en estado PENDIENTE (0)
        if (ordenDestino.getEstado() == null || !ordenDestino.getEstado().equals(0)) {
            throw new BusinessLogicException("La orden destino debe estar en estado PENDIENTE");
        }

        // Obtener todos los detalles de la orden origen
        List<DetalleOrden> detallesOrigen = detalleOrdenRepository.findByOrdenCompraOrdenCompraId(ordenOrigenId);
        
        List<DetalleOrdenDTO> detallesDuplicados = new java.util.ArrayList<>();

        for (DetalleOrden detalleOrigen : detallesOrigen) {
            // Verificar que no exista ya este producto en la orden destino
            List<DetalleOrden> detallesDestino = detalleOrdenRepository.findByOrdenCompraOrdenCompraId(ordenDestinoId);
            boolean productoYaExiste = detallesDestino.stream()
                    .anyMatch(d -> d.getProducto().getProductoId().equals(detalleOrigen.getProducto().getProductoId()));
            
            if (!productoYaExiste) {
                // Crear nuevo detalle para la orden destino
                DetalleOrden nuevoDetalle = new DetalleOrden();
                nuevoDetalle.setOrdenCompra(ordenDestino);
                nuevoDetalle.setProducto(detalleOrigen.getProducto());
                nuevoDetalle.setCantidad(detalleOrigen.getCantidad());
                nuevoDetalle.setPrecioUnitario(detalleOrigen.getPrecioUnitario());
                nuevoDetalle.setSubtotal(detalleOrigen.getSubtotal());

                DetalleOrden savedDetalle = detalleOrdenRepository.save(nuevoDetalle);
                detallesDuplicados.add(convertToDTO(savedDetalle));
            }
        }

        return detallesDuplicados;
    }

    private void validarDetalleOrden(DetalleOrdenDTO detalleDTO) {
        if (detalleDTO.getOrdenCompraId() == null) {
            throw new BusinessLogicException("La orden de compra es obligatoria");
        }

        if (detalleDTO.getProducto() == null || detalleDTO.getProducto().getProductoId() == null) {
            throw new BusinessLogicException("El producto es obligatorio");
        }

        if (detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {
            throw new BusinessLogicException("La cantidad debe ser mayor a cero");
        }

        if (detalleDTO.getPrecioUnitario() == null || detalleDTO.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessLogicException("El precio unitario debe ser mayor a cero");
        }
    }

    private BigDecimal calcularSubtotal(Integer cantidad, BigDecimal precioUnitario) {
        if (cantidad == null || precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(new BigDecimal(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    private DetalleOrdenDTO convertToDTO(DetalleOrden detalle) {
        // Simplificar la conversión para evitar dependencias circulares
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setProductoId(detalle.getProducto().getProductoId());
        productoDTO.setNombre(detalle.getProducto().getNombre());
        productoDTO.setUnidadMedida(detalle.getProducto().getUnidadMedida());

        return new DetalleOrdenDTO(
                detalle.getDetalleId(),
                detalle.getOrdenCompra().getOrdenCompraId(),
                productoDTO,
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()
        );
    }

    private DetalleOrden convertToEntity(DetalleOrdenDTO dto, OrdenCompra orden, Producto producto) {
        DetalleOrden detalle = new DetalleOrden();
        detalle.setDetalleId(dto.getDetalleId());
        detalle.setOrdenCompra(orden);
        detalle.setProducto(producto);
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(dto.getPrecioUnitario());
        // El subtotal se calcula automáticamente en el método save
        return detalle;
    }
}