package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.RecetaProductoDTO;
import com.system.wasimikuna.dto.PlatoDTO;
import com.system.wasimikuna.dto.ProductoDTO;
import com.system.wasimikuna.exception.BusinessLogicException;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.RecetaProducto;
import com.system.wasimikuna.model.Plato;
import com.system.wasimikuna.model.Producto;
import com.system.wasimikuna.repository.RecetaProductoRepository;
import com.system.wasimikuna.repository.PlatoRepository;
import com.system.wasimikuna.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecetaProductoService {

    private final RecetaProductoRepository recetaRepository;
    private final PlatoRepository platoRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<RecetaProductoDTO> findAll() {
        return recetaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecetaProductoDTO findById(Long id) {
        RecetaProducto receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta Producto", "id", id));
        return convertToDTO(receta);
    }

    @Transactional(readOnly = true)
    public List<RecetaProductoDTO> findByPlato(Long platoId) {
        return recetaRepository.findByPlatoPlatoId(platoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecetaProductoDTO> findByProducto(Long productoId) {
        return recetaRepository.findByProductoProductoId(productoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalIngredientesPlato(Long platoId) {
        return recetaRepository.getTotalIngredientsByPlato(platoId);
    }

    @Transactional(readOnly = true)
    public List<RecetaProductoDTO> findByPlatoYCategoria(Long platoId, Producto.CategoriaProducto categoria) {
        return recetaRepository.findByPlatoAndProductoCategoria(platoId, categoria).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Plato> findPlatosByProducto(Long productoId) {
        return recetaRepository.findPlatosByProducto(productoId);
    }

    @Transactional(readOnly = true)
    public List<RecetaProductoDTO> findByCantidadMinima(BigDecimal cantidadMinima) {
        return recetaRepository.findByCantidadPorRacionGreaterThanEqual(cantidadMinima).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RecetaProductoDTO save(RecetaProductoDTO recetaDTO) {
        // Validar que el plato existe
        Plato plato = platoRepository.findById(recetaDTO.getPlato().getPlatoId())
                .orElseThrow(() -> new ResourceNotFoundException("Plato", "id", recetaDTO.getPlato().getPlatoId()));

        // Validar que el producto existe
        Producto producto = productoRepository.findById(recetaDTO.getProducto().getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", recetaDTO.getProducto().getProductoId()));

        // Validar datos de la receta
        validarReceta(recetaDTO);

        // Verificar que no exista ya esta combinación plato-producto
        List<RecetaProducto> existentes = recetaRepository.findByPlatoPlatoId(plato.getPlatoId());
        boolean yaExiste = existentes.stream()
                .anyMatch(r -> r.getProducto().getProductoId().equals(producto.getProductoId()));
        
        if (yaExiste) {
            throw new BusinessLogicException("Ya existe una receta para este plato y producto");
        }

        RecetaProducto receta = convertToEntity(recetaDTO, plato, producto);
        RecetaProducto savedReceta = recetaRepository.save(receta);
        return convertToDTO(savedReceta);
    }

    public RecetaProductoDTO update(Long id, RecetaProductoDTO recetaDTO) {
        RecetaProducto existingReceta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta Producto", "id", id));

        // Validar datos
        validarReceta(recetaDTO);

        // Verificar duplicados solo si cambió el producto
        if (!existingReceta.getProducto().getProductoId().equals(recetaDTO.getProducto().getProductoId())) {
            List<RecetaProducto> existentes = recetaRepository.findByPlatoPlatoId(existingReceta.getPlato().getPlatoId());
            boolean yaExiste = existentes.stream()
                    .anyMatch(r -> r.getProducto().getProductoId().equals(recetaDTO.getProducto().getProductoId()) 
                            && !r.getRecetaId().equals(id));
            
            if (yaExiste) {
                throw new BusinessLogicException("Ya existe una receta para este plato y producto");
            }
            
            // Actualizar producto si cambió
            Producto producto = productoRepository.findById(recetaDTO.getProducto().getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", recetaDTO.getProducto().getProductoId()));
            existingReceta.setProducto(producto);
        }

        existingReceta.setCantidadPorRacion(recetaDTO.getCantidadPorRacion());

        RecetaProducto updatedReceta = recetaRepository.save(existingReceta);
        return convertToDTO(updatedReceta);
    }

    public RecetaProductoDTO actualizarCantidad(Long id, BigDecimal nuevaCantidad) {
        RecetaProducto receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta Producto", "id", id));

        if (nuevaCantidad == null || nuevaCantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessLogicException("La cantidad debe ser mayor a cero");
        }

        receta.setCantidadPorRacion(nuevaCantidad);
        RecetaProducto updatedReceta = recetaRepository.save(receta);
        return convertToDTO(updatedReceta);
    }

    public void deleteById(Long id) {
        if (!recetaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Receta Producto", "id", id);
        }
        recetaRepository.deleteById(id);
    }

    public void deleteByPlatoId(Long platoId) {
        List<RecetaProducto> recetas = recetaRepository.findByPlatoPlatoId(platoId);
        recetaRepository.deleteAll(recetas);
    }

    public List<RecetaProductoDTO> clonarReceta(Long platoOrigenId, Long platoDestinoId) {
        // Validar que ambos platos existen
        if (!platoRepository.existsById(platoOrigenId)) {
            throw new ResourceNotFoundException("Plato Origen", "id", platoOrigenId);
        }
        
        Plato platoDestino = platoRepository.findById(platoDestinoId)
                .orElseThrow(() -> new ResourceNotFoundException("Plato Destino", "id", platoDestinoId));

        // Obtener todas las recetas del plato origen
        List<RecetaProducto> recetasOrigen = recetaRepository.findByPlatoPlatoId(platoOrigenId);
        
        List<RecetaProductoDTO> recetasClonadas = new java.util.ArrayList<>();

        for (RecetaProducto recetaOrigen : recetasOrigen) {
            // Verificar que no exista ya esta combinación en el plato destino
            List<RecetaProducto> existentesDestino = recetaRepository.findByPlatoPlatoId(platoDestino.getPlatoId());
            boolean yaExiste = existentesDestino.stream()
                    .anyMatch(r -> r.getProducto().getProductoId().equals(recetaOrigen.getProducto().getProductoId()));
            
            if (!yaExiste) {
                // Crear nueva receta para el plato destino
                RecetaProducto nuevaReceta = new RecetaProducto();
                nuevaReceta.setPlato(platoDestino);
                nuevaReceta.setProducto(recetaOrigen.getProducto());
                nuevaReceta.setCantidadPorRacion(recetaOrigen.getCantidadPorRacion());

                RecetaProducto savedReceta = recetaRepository.save(nuevaReceta);
                recetasClonadas.add(convertToDTO(savedReceta));
            }
        }

        return recetasClonadas;
    }

    private void validarReceta(RecetaProductoDTO recetaDTO) {
        if (recetaDTO.getPlato() == null || recetaDTO.getPlato().getPlatoId() == null) {
            throw new BusinessLogicException("El plato es obligatorio");
        }

        if (recetaDTO.getProducto() == null || recetaDTO.getProducto().getProductoId() == null) {
            throw new BusinessLogicException("El producto es obligatorio");
        }

        if (recetaDTO.getCantidadPorRacion() == null || recetaDTO.getCantidadPorRacion().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessLogicException("La cantidad por ración debe ser mayor a cero");
        }
    }

    private RecetaProductoDTO convertToDTO(RecetaProducto receta) {
        // Simplificar la conversión para evitar dependencias circulares
        PlatoDTO platoDTO = new PlatoDTO();
        platoDTO.setPlatoId(receta.getPlato().getPlatoId());
        platoDTO.setNombre(receta.getPlato().getNombre());

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setProductoId(receta.getProducto().getProductoId());
        productoDTO.setNombre(receta.getProducto().getNombre());
        productoDTO.setUnidadMedida(receta.getProducto().getUnidadMedida());

        return new RecetaProductoDTO(
                receta.getRecetaId(),
                platoDTO,
                productoDTO,
                receta.getCantidadPorRacion()
        );
    }

    private RecetaProducto convertToEntity(RecetaProductoDTO dto, Plato plato, Producto producto) {
        RecetaProducto receta = new RecetaProducto();
        receta.setRecetaId(dto.getRecetaId());
        receta.setPlato(plato);
        receta.setProducto(producto);
        receta.setCantidadPorRacion(dto.getCantidadPorRacion());
        return receta;
    }
}