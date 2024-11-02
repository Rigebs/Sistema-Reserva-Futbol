package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.DetalleVentaDTO;
import com.cruz_sur.api.repository.CampoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservaCalculations {

    private final CampoRepository campoRepository;

    public BigDecimal calculateSubtotal(List<DetalleVentaDTO> detallesVenta) {
        return detallesVenta.stream()
                .map(detalle -> campoRepository.findById(detalle.getCampoId())
                        .orElseThrow(() -> new RuntimeException("Campo not found for id: " + detalle.getCampoId()))
                        .getPrecio())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateDiscount(BigDecimal subtotal, BigDecimal descuento) {
        return (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0)
                ? subtotal.multiply(descuento).divide(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
    }

    public BigDecimal calculateIgv(BigDecimal subtotalAfterDiscount, BigDecimal igv) {
        return (igv != null && igv.compareTo(BigDecimal.ZERO) > 0)
                ? subtotalAfterDiscount.multiply(igv).divide(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
    }
}
