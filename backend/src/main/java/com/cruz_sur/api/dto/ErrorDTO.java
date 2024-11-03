package com.cruz_sur.api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {
    private String error;
}
