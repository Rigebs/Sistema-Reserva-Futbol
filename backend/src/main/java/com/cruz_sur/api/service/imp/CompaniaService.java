package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.CompaniaDTO;
import com.cruz_sur.api.model.Compania;
import com.cruz_sur.api.model.Imagen;
import com.cruz_sur.api.repository.CompaniaRepository;
import com.cruz_sur.api.service.ICompaniaService;
import com.cruz_sur.api.service.IImagenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompaniaService implements ICompaniaService {
    private final CompaniaRepository companiaRepository;
    private final IImagenService iImagenService;

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public Compania save(Compania compania, MultipartFile file) throws IOException {
        String authenticatedUsername = getAuthenticatedUsername();
        compania.setUsuarioCreacion(authenticatedUsername); // Guardar el usuario que crea la compañía
        compania.setFechaCreacion(LocalDateTime.now()); // Establecer la fecha de creación
        compania.setEstado('1'); // Asumiendo que '1' significa activo

        if (file != null && !file.isEmpty()) {
            Imagen imagen = iImagenService.uploadImage(file);
            compania.setImagen(imagen);
        }

        return companiaRepository.save(compania);
    }

    @Override
    public Compania update(Long id, Compania compania) {
        Compania companiaExistente = companiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compañía no encontrada"));

        // Establecer los nuevos valores
        companiaExistente.setNombre(compania.getNombre());
        companiaExistente.setConcepto(compania.getConcepto());
        companiaExistente.setCorreo(compania.getCorreo());
        companiaExistente.setPagWeb(compania.getPagWeb());

        // Actualizar el usuario de modificación y fecha
        String authenticatedUsername = getAuthenticatedUsername();
        companiaExistente.setUsuarioModificacion(authenticatedUsername); // Actualizar el usuario que modifica
        companiaExistente.setFechaModificacion(LocalDateTime.now()); // Actualizar la fecha de modificación

        // Actualiza la imagen si se proporciona
        if (compania.getImagen() != null) {
            companiaExistente.setImagen(compania.getImagen());
        }

        return companiaRepository.save(companiaExistente);
    }



    @Override
    public Optional<Compania> byId(Long id) {
        return companiaRepository.findById(id);
    }

    @Override
    public Compania changeStatus(Long id, Integer status) {
        Compania compania = companiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compañía no encontrada"));

        compania.setEstado(status == 1 ? '1' : '0');
        return companiaRepository.save(compania);
    }

    public Compania updateCompaniaImage(MultipartFile file, Compania compania) throws IOException {
        if (file != null && !file.isEmpty()) {
            Imagen nuevaImagen = iImagenService.uploadImage(file);
            compania.setImagen(nuevaImagen);

            // Aquí se puede actualizar el usuario de modificación si se desea
            String authenticatedUsername = getAuthenticatedUsername();
            compania.setUsuarioModificacion(authenticatedUsername);
            compania.setFechaModificacion(LocalDateTime.now());
        }
        return companiaRepository.save(compania);
    }
    @Override
    public List<CompaniaDTO> all() {
        return companiaRepository.findAll().stream()
                .map(compania -> new CompaniaDTO(
                        compania.getId(),
                        compania.getNombre(),
                        compania.getConcepto(),
                        compania.getCorreo(),
                        compania.getPagWeb(),
                        compania.getImagen() != null ? compania.getImagen().getImageUrl() : null // Obtiene la URL de la imagen si existe
                ))
                .collect(Collectors.toList());
    }

}
