package com.cruz_sur.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompaniaDTO {
    private Long id;
    private String nombre;
    private String concepto;
    private String correo;
    private String pagWeb;
    private String imageUrl;




    
}
