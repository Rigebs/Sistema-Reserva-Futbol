package com.cruz_sur.api.service;

import com.cruz_sur.api.model.Imagen;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IImagenService {
    Imagen uploadImage(MultipartFile file) throws IOException;
    void deleteImage(Imagen imagen) throws IOException;
    Imagen findById(Long id) throws IOException;
    List<Imagen> findAll() throws IOException;
    List<Imagen> findAllActive() throws IOException;
    Imagen updateImage(Long id, MultipartFile file) throws IOException;
    Imagen changeStatus(Long id, Integer status);
}
