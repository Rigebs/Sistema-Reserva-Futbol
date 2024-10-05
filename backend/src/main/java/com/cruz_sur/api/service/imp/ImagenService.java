package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Imagen;
import com.cruz_sur.api.repository.ImagenRepository;
import com.cruz_sur.api.service.ICloudinaryService;
import com.cruz_sur.api.service.IImagenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ImagenService implements IImagenService {
    private final ICloudinaryService iCloudinaryService;
    private final ImagenRepository imageRepository;

    @Override
    public Imagen uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no debe estar vacío.");
        }

        Map uploadResult = iCloudinaryService.upload(file);
        String imageUrl = (String) uploadResult.get("url");
        String imageId = (String) uploadResult.get("public_id");

        // Asegúrate de que el nombre no sea nulo
        String name = file.getOriginalFilename();
        if (name == null) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser nulo.");
        }

        Imagen image = new Imagen(name, imageUrl, imageId);
        image.setEstado('1');
        return imageRepository.save(image);
    }

    @Override
    public void deleteImage(Imagen imagen) throws IOException {
        iCloudinaryService.delete(imagen.getImageId());
        imageRepository.deleteById(imagen.getId());
    }

    @Override
    public Imagen findById(Long id) throws IOException {
        return imageRepository.findById(id).orElse(null);
    }

    @Override
    public List<Imagen> findAll() throws IOException {
        return imageRepository.findAll();
    }

    @Override
    public List<Imagen> findAllActive() throws IOException {
        return imageRepository.findByEstado('1');
    }

    @Override
    public Imagen updateImage(Long id, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no debe estar vacío.");
        }

        Imagen existingImage = imageRepository.findById(id)
                .orElseThrow(() -> new IOException("Imagen no encontrada"));

        iCloudinaryService.delete(existingImage.getImageId());

        Map uploadResult = iCloudinaryService.upload(file);
        String newImageUrl = (String) uploadResult.get("url");
        String newImageId = (String) uploadResult.get("public_id");

        existingImage.setImageUrl(newImageUrl);
        existingImage.setImageId(newImageId);
        existingImage.setName(file.getOriginalFilename());
        return imageRepository.save(existingImage);
    }

    @Override
    public Imagen changeStatus(Long id, Integer status) {
        Imagen imagen = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));

        imagen.setEstado(status == 1 ? '1' : '0');
        return imageRepository.save(imagen);
    }


}
