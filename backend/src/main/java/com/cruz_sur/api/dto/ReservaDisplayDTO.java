package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaDisplayDTO {
    private Long reservaId;
    private String sede;
    private LocalDate fechaReserva;
    private BigDecimal subtotal;
    private BigDecimal total;
    private String cliente;
}
