package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.DetalleVenta;
import com.cruz_sur.api.repository.DetalleVentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CampoAvailabilityService {
    private final DetalleVentaRepository detalleVentaRepository;

    // Check campo availability based on DetalleVenta.date
    public boolean isCampoAvailable(Long campoId, LocalDate detalleFecha, Time horaInicio, Time horaFin) {
        if (horaFin.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return false; // Prevent bookings that end at midnight
        }

        List<DetalleVenta> reservas = detalleVentaRepository.findByCampoIdAndFecha(campoId, detalleFecha)
                .stream()
                .filter(detalle -> detalle.getEstado() == '1') // Only consider active bookings
                .toList();

        for (DetalleVenta reserva : reservas) {
            // Check for overlapping time intervals
            if (reserva.getHoraInicio().before(horaFin) && reserva.getHoraFinal().after(horaInicio)) {
                return false;
            }
        }
        return true;
    }

    // Remove unavailable hours for the given date and campo
    public List<Time> getAvailableHours(Long campoId, LocalDate detalleFecha, List<Time> allHours) {
        List<DetalleVenta> reservas = detalleVentaRepository.findByCampoIdAndFecha(campoId, detalleFecha);
        List<Time> availableHours = new ArrayList<>(allHours);

        for (DetalleVenta reserva : reservas) {
            // Remove hours that are already reserved
            availableHours.removeIf(hour ->
                    !hour.before(reserva.getHoraInicio()) && !hour.after(reserva.getHoraFinal()));
        }

        return availableHours;
    }
}