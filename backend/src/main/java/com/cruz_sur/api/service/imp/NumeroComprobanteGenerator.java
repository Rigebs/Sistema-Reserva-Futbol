package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.repository.BoletaRepository;
import com.cruz_sur.api.repository.FacturaRepository;
import com.cruz_sur.api.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class NumeroComprobanteGenerator {

    private final BoletaRepository boletaRepository;
    private final FacturaRepository facturaRepository;
    private final TicketRepository ticketRepository;

    // Maps to keep track of the last number for each user and comprobante type
    private final Map<Long, Long> lastBoletaNumbers = new HashMap<>();
    private final Map<Long, Long> lastFacturaNumbers = new HashMap<>();
    private final Map<Long, Long> lastTicketNumbers = new HashMap<>();

    public String generate(Long userId, Character tipoComprobante) {
        long nextNumber;

        switch (tipoComprobante) {
            case 'B':
                nextNumber = lastBoletaNumbers.getOrDefault(userId, 0L) + 1;
                lastBoletaNumbers.put(userId, nextNumber); // Update the map
                break;
            case 'F':
                nextNumber = lastFacturaNumbers.getOrDefault(userId, 0L) + 1;
                lastFacturaNumbers.put(userId, nextNumber); // Update the map
                break;
            case 'T':
                nextNumber = lastTicketNumbers.getOrDefault(userId, 0L) + 1;
                lastTicketNumbers.put(userId, nextNumber); // Update the map
                break;
            default:
                throw new IllegalArgumentException("Invalid tipoComprobante: " + tipoComprobante);
        }

        // Format the number to 6 digits
        return String.format("%06d", nextNumber);
    }
}
