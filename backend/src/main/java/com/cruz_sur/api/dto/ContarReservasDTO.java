package com.cruz_sur.api.dto;

import lombok.Data;

@Data
public class ContarReservasDTO {
    private int totalReservas;
    private String campo;       // Nombre del campo si se usa en 'campos_reservados'
    private double TotalMonetario;
}
