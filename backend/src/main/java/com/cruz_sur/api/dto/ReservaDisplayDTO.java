package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaDisplayDTO {
    private Long reservaId;
    private String sede;
    private LocalDateTime fechaReserva;
    private BigDecimal subtotal;
    private BigDecimal total;
    private String cliente;
}
