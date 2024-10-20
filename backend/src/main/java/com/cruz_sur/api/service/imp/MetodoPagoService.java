package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.MetodoPago;
import com.cruz_sur.api.repository.MetodoPagoRepository;
import com.cruz_sur.api.service.IMetodoPagoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MetodoPagoService implements IMetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    @Override
    public MetodoPago save(MetodoPago metodoPago) {
        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    public MetodoPago update(Long id, MetodoPago metodoPago) {
        MetodoPago metodoExistente = metodoPagoRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Método de pago no encontrado")
        );
        metodoExistente.setNombre(metodoPago.getNombre());
        metodoExistente.setUsuarioCreacion(metodoPago.getUsuarioCreacion());
        metodoExistente.setFechaCreacion(metodoPago.getFechaCreacion());
        metodoExistente.setUsuarioModificacion(metodoPago.getUsuarioModificacion());
        metodoExistente.setFechaModificacion(metodoPago.getFechaModificacion());

        return metodoPagoRepository.save(metodoExistente);
    }

    @Override
    public List<MetodoPago> all() {
        return metodoPagoRepository.findAll();
    }

    @Override
    public MetodoPago changeStatus(Long id, Integer status) {
        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        metodoPago.setEstado(status == 1 ? '1' : '0');
        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    public Optional<MetodoPago> byId(Long id) {
        return metodoPagoRepository.findById(id);
    }
}
