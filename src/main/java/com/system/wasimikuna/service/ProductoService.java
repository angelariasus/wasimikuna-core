package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.ProductoDTO;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.Producto;
import com.system.wasimikuna.model.Producto.CategoriaProducto;
import com.system.wasimikuna.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoDTO findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        return convertToDTO(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findByNombre(String nombre) {
        return productoRepository.findByNombreContaining(nombre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findByCategoria(String categoria) {
        CategoriaProducto categoriaEnum = CategoriaProducto.valueOf(categoria.toUpperCase());
        return productoRepository.findByCategoria(categoriaEnum).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findByRequiereRefrigeracion(boolean requiere) {
        return productoRepository.findByRequiereRefrigeracion(requiere ? 1 : 0).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> findByVidaUtilMaxima(Integer diasMaximos) {
        return productoRepository.findByVidaUtilMenorIgual(diasMaximos).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO save(ProductoDTO productoDTO) {
        Producto producto = convertToEntity(productoDTO);
        Producto savedProducto = productoRepository.save(producto);
        return convertToDTO(savedProducto);
    }

    public ProductoDTO update(Long id, ProductoDTO productoDTO) {
        Producto existingProducto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        existingProducto.setNombre(productoDTO.getNombre());
        existingProducto.setUnidadMedida(productoDTO.getUnidadMedida());
        existingProducto.setCategoria(productoDTO.getCategoria() != null ? 
            CategoriaProducto.valueOf(productoDTO.getCategoria().toUpperCase()) : null);
        existingProducto.setVidaUtilDias(productoDTO.getVidaUtilDias());
        existingProducto.setRequiereRefrigeracion(productoDTO.getRequiereRefrigeracion());
        existingProducto.setDescripcion(productoDTO.getDescripcion());

        Producto updatedProducto = productoRepository.save(existingProducto);
        return convertToDTO(updatedProducto);
    }

    public void deleteById(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", "id", id);
        }
        productoRepository.deleteById(id);
    }

    private ProductoDTO convertToDTO(Producto producto) {
        return new ProductoDTO(
                producto.getProductoId(),
                producto.getNombre(),
                producto.getUnidadMedida(),
                producto.getCategoria() != null ? producto.getCategoria().name() : null,
                producto.getVidaUtilDias(),
                producto.getRequiereRefrigeracion(),
                producto.getDescripcion()
        );
    }

    private Producto convertToEntity(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setProductoId(dto.getProductoId());
        producto.setNombre(dto.getNombre());
        producto.setUnidadMedida(dto.getUnidadMedida());
        producto.setCategoria(dto.getCategoria() != null ? 
            CategoriaProducto.valueOf(dto.getCategoria().toUpperCase()) : null);
        producto.setVidaUtilDias(dto.getVidaUtilDias());
        producto.setRequiereRefrigeracion(dto.getRequiereRefrigeracion());
        producto.setDescripcion(dto.getDescripcion());
        return producto;
    }
}