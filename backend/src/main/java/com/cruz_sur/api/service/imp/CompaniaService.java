package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.PagoInfoDTO;
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

@Service
@AllArgsConstructor
public class CompaniaService implements ICompaniaService {
    private final CompaniaRepository companiaRepository;
    private final IImagenService iImagenService;

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public Compania save(Compania compania, MultipartFile file, MultipartFile qrFile) throws IOException {
        String authenticatedUsername = getAuthenticatedUsername();
        compania.setUsuarioCreacion(authenticatedUsername);
        compania.setFechaCreacion(LocalDateTime.now());
        compania.setEstado('0');
        if (file != null && !file.isEmpty()) {
            Imagen imagen = iImagenService.uploadImage(file);
            compania.setImagen(imagen);
        }

        if (qrFile != null && !qrFile.isEmpty()) {
            Imagen qrImagen = iImagenService.uploadImage(qrFile);
            compania.setQrImagen(qrImagen);
        }

        return companiaRepository.save(compania);
    }

    @Override
    public Compania update(Long id, Compania compania) {
        Compania companiaExistente = companiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compañía no encontrada"));

        companiaExistente.setNombre(compania.getNombre());
        companiaExistente.setConcepto(compania.getConcepto());
        companiaExistente.setCorreo(compania.getCorreo());

        String authenticatedUsername = getAuthenticatedUsername();
        companiaExistente.setUsuarioModificacion(authenticatedUsername);
        companiaExistente.setFechaModificacion(LocalDateTime.now());

        if (compania.getImagen() != null) {
            companiaExistente.setImagen(compania.getImagen());
        }

        if (compania.getQrImagen() != null) {
            companiaExistente.setQrImagen(compania.getQrImagen());
        }

        return companiaRepository.save(companiaExistente);
    }

    @Override
    public List<Compania> all() {
        return companiaRepository.findAll();
    }

    @Override
    public Optional<Compania> byId(Long id) {
        return companiaRepository.findById(id);
    }

    // Método para cambiar el estado de una compañía
    @Override
    public Compania changeStatus(Long id, Integer status) {
        Compania compania = companiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compañía no encontrada"));

        compania.setEstado(status == 1 ? '1' : '0');
        return companiaRepository.save(compania);
    }

    @Override
    public Compania updateCompaniaImages(MultipartFile file, MultipartFile qrFile, Compania compania) throws IOException {
        if (file != null && !file.isEmpty()) {
            Imagen nuevaImagen = iImagenService.uploadImage(file);
            compania.setImagen(nuevaImagen);
        }

        if (qrFile != null && !qrFile.isEmpty()) {
            Imagen nuevaQrImagen = iImagenService.uploadImage(qrFile);
            compania.setQrImagen(nuevaQrImagen);
        }

        String authenticatedUsername = getAuthenticatedUsername();
        compania.setUsuarioModificacion(authenticatedUsername);
        compania.setFechaModificacion(LocalDateTime.now());

        return companiaRepository.save(compania);
    }

    @Override
    public PagoInfoDTO getPagoInfoByCompaniaId(Long companiaId) {
        Optional<Compania> companiaOptional = companiaRepository.findById(companiaId);
        if (!companiaOptional.isPresent()) {
            throw new RuntimeException("Compañía no encontrada");
        }
        Compania compania = companiaOptional.get();
        String celular = compania.getCelular();
        String qrImagenUrl = null;
        if (compania.getQrImagen() != null) {
            qrImagenUrl = compania.getQrImagen().getImageUrl();
        }
        return new PagoInfoDTO(celular, qrImagenUrl);
    }
}
