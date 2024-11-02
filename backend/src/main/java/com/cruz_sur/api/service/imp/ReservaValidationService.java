package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Cliente;
import org.springframework.stereotype.Service;

@Service
public class ReservaValidationService {

    public void validateTipoComprobante(Cliente cliente, Character tipoComprobante) {
        if (tipoComprobante == null) {
            throw new RuntimeException("Tipo de comprobante no especificado");
        }

        boolean isEmpresa = cliente.getEmpresa() != null;
        boolean isPersona = cliente.getPersona() != null;

        switch (tipoComprobante) {
            case 'F':
                if (!isEmpresa) {
                    throw new RuntimeException("El tipo de comprobante 'F' solo es válido para empresas.");
                }
                break;
            case 'B':
                if (!isPersona) {
                    throw new RuntimeException("El tipo de comprobante 'B' solo es válido para personas.");
                }
                break;
            case 'T':
                if (!isEmpresa && !isPersona) {
                    throw new RuntimeException("El tipo de comprobante 'T' es válido solo si existe una empresa o una persona.");
                }
                break;
            default:
                throw new RuntimeException("Tipo de comprobante no válido: debe ser 'F', 'B' o 'T'.");
        }
    }
}
