package com.cruz_sur.api.service;

import com.cruz_sur.api.dto.PagoInfoDTO;
import com.cruz_sur.api.model.Compania;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ICompaniaService {
    Compania save(Compania compania, MultipartFile file, MultipartFile qrFile) throws IOException;
    Compania update(Long id, Compania compania);
    List<Compania> all();
    Optional<Compania> byId(Long id);
    Compania changeStatus(Long id, Integer status);
    Compania updateCompaniaImages(MultipartFile file, MultipartFile qrFile, Compania compania) throws IOException;

    PagoInfoDTO getPagoInfoByCompaniaId(Long companiaId);
}
