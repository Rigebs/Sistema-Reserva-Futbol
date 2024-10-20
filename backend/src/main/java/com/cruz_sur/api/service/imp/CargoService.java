package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Cargo;
import com.cruz_sur.api.repository.CargoRepository;
import com.cruz_sur.api.service.ICargoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CargoService implements ICargoService {

    private final CargoRepository cargoRepository;
    @Override
    public Cargo save(Cargo cargo) {
        return cargoRepository.save(cargo);
    }

    @Override
    public Cargo update(Long id, Cargo cargo) {
        Cargo cargo1 = cargoRepository.findById(id).orElseThrow(
                ()->new RuntimeException("Cargo no encontrado")
        );
        cargo1.setNombreCargo(cargo.getNombreCargo());
        cargo1.setUsuarioCreacion(cargo.getUsuarioCreacion());
        cargo1.setFechaCreacion(cargo.getFechaCreacion());
        cargo1.setUsuarioModificacion(cargo.getUsuarioModificacion());
        cargo1.setFechaModificacion(cargo.getFechaModificacion());

        return cargoRepository.save(cargo1);
    }

    @Override
    public List<Cargo> all() {
        return List.of();
    }

    @Override
    public Cargo changeStatus(Long id, Integer status) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("cargo no encontrada"));

        cargo.setEstado(status == 1 ? '1' : '0');
        return cargoRepository.save(cargo);
    }

    @Override
    public Optional<Cargo> byId(Long id) {
        return cargoRepository.findById(id);
    }
}
