package com.system.wasimikuna.service;

import com.system.wasimikuna.dto.PlatoDTO;
import com.system.wasimikuna.exception.ResourceNotFoundException;
import com.system.wasimikuna.model.Plato;
import com.system.wasimikuna.repository.PlatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlatoService {

    private final PlatoRepository platoRepository;

    @Transactional(readOnly = true)
    public List<PlatoDTO> findAll() {
        return platoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlatoDTO findById(Long id) {
        Plato plato = platoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plato", "id", id));
        return convertToDTO(plato);
    }

    @Transactional(readOnly = true)
    public List<PlatoDTO> findByNombre(String nombre) {
        return platoRepository.findByNombreContaining(nombre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlatoDTO> findByRegion(String region) {
        return platoRepository.findByRegionOrigen(region).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlatoDTO> findByAporteCaloricoMinimo(BigDecimal minCalorias) {
        return platoRepository.findByAporteCaloricoGreaterThanEqual(minCalorias).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlatoDTO> findByAporteProteicoMinimo(BigDecimal minProteina) {
        return platoRepository.findByAporteProteicoGreaterThanEqual(minProteina).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlatoDTO> findByRangoCaloricoCartas(BigDecimal minCalorias, BigDecimal maxCalorias) {
        return platoRepository.findByAporteCaloricoRange(minCalorias, maxCalorias).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PlatoDTO save(PlatoDTO platoDTO) {
        Plato plato = convertToEntity(platoDTO);
        Plato savedPlato = platoRepository.save(plato);
        return convertToDTO(savedPlato);
    }

    public PlatoDTO update(Long id, PlatoDTO platoDTO) {
        Plato existingPlato = platoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plato", "id", id));

        existingPlato.setNombre(platoDTO.getNombre());
        existingPlato.setAporteCalorico(platoDTO.getAporteCalorico());
        existingPlato.setAporteProteico(platoDTO.getAporteProteico());
        existingPlato.setAporteHierro(platoDTO.getAporteHierro());
        existingPlato.setRegionOrigen(platoDTO.getRegionOrigen());
        existingPlato.setRecetaTexto(platoDTO.getRecetaTexto());

        Plato updatedPlato = platoRepository.save(existingPlato);
        return convertToDTO(updatedPlato);
    }

    public void deleteById(Long id) {
        if (!platoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plato", "id", id);
        }
        platoRepository.deleteById(id);
    }

    private PlatoDTO convertToDTO(Plato plato) {
        return new PlatoDTO(
                plato.getPlatoId(),
                plato.getNombre(),
                plato.getAporteCalorico(),
                plato.getAporteProteico(),
                plato.getAporteHierro(),
                plato.getRegionOrigen(),
                plato.getRecetaTexto()
        );
    }

    private Plato convertToEntity(PlatoDTO dto) {
        Plato plato = new Plato();
        plato.setPlatoId(dto.getPlatoId());
        plato.setNombre(dto.getNombre());
        plato.setAporteCalorico(dto.getAporteCalorico());
        plato.setAporteProteico(dto.getAporteProteico());
        plato.setAporteHierro(dto.getAporteHierro());
        plato.setRegionOrigen(dto.getRegionOrigen());
        plato.setRecetaTexto(dto.getRecetaTexto());
        return plato;
    }
}