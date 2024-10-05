package com.cruz_sur.api.controller;

import com.cruz_sur.api.model.Imagen;
import com.cruz_sur.api.service.IImagenService;
import com.cruz_sur.api.service.imp.ImagenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
public class ImagenController {

    private final IImagenService imagenService;

    @PostMapping("/upload")
    public ResponseEntity<Imagen> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Imagen imagen = imagenService.uploadImage(file);
            return new ResponseEntity<>(imagen, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        try {
            Imagen imagen = imagenService.findById(id);
            if (imagen == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            imagenService.deleteImage(imagen);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Imagen> getImageById(@PathVariable Long id) {
        try {
            Imagen imagen = imagenService.findById(id);
            if (imagen != null) {
                return new ResponseEntity<>(imagen, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Imagen>> findAll() {
        try {
            List<Imagen> images = imagenService.findAll();
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Imagen> updateImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Imagen updatedImage = imagenService.updateImage(id, file);
            return new ResponseEntity<>(updatedImage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<Imagen>> findAllActive() {
        try {
            List<Imagen> images = imagenService.findAllActive();
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id, @PathVariable Integer status) {
        imagenService.changeStatus(id, status);
        String statusMessage = status == 1 ? "activado" : "desactivado";
        return new ResponseEntity<>("Imagen " + statusMessage + " con éxito", HttpStatus.OK);
    }
}
