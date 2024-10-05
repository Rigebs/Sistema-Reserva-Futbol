package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Compania;
import com.cruz_sur.api.model.Imagen;
import com.cruz_sur.api.repository.CompaniaRepository;
import com.cruz_sur.api.service.ICompaniaService;
import com.cruz_sur.api.service.IImagenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CompaniaService implements ICompaniaService {
    private final CompaniaRepository companiaRepository;
    private final IImagenService iImagenService;

    @Override
    public Compania save(Compania compania, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()){
            Imagen imagen = iImagenService.uploadImage(file);
            compania.setImagen(imagen);
        }
        return companiaRepository.save(compania);
    }

    @Override
    public Compania update(Long id, Compania compania) {
        Compania compania1 = companiaRepository.findById(id).orElseThrow(
                ()->new RuntimeException("Imagen no encontrada")
        );
        compania1.setNombre(compania.getNombre());
        compania1.setConcepto(compania.getConcepto());
        compania1.setCorreo(compania.getCorreo());
        compania1.setPagWeb(compania.getPagWeb());
        compania1.setUsuarioCreacion(compania.getUsuarioCreacion());
        compania1.setUsuarioModificacion(compania.getUsuarioModificacion());
        compania1.setFechaModificacion(compania.getFechaModificacion());
        compania1.setImagen(compania.getImagen());
        return companiaRepository.save(compania1);
    }

    @Override
    public List<Compania> all() {
        return companiaRepository.findAll();
    }

    @Override
    public Optional<Compania> byId(Long id) {
        return companiaRepository.findById(id);
    }

    @Override
    public Compania changeStatus(Long id, Integer status) {
        Compania compania = companiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("compania no encontrada"));

        compania.setEstado(status == 1 ? '1' : '0');
        return companiaRepository.save(compania);
    }

    @Override
    public Compania updateCompaniaImage(MultipartFile file, Compania compania) throws IOException {
        if (compania.getImagen() != null) {
            iImagenService.deleteImage(compania.getImagen());
        }
        Imagen newImage = iImagenService.uploadImage(file);
        compania.setImagen(newImage);
        return companiaRepository.save(compania);
    }
}
