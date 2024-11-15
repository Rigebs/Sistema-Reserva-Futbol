package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.ContarReservasDTO;
import com.cruz_sur.api.dto.VentasMensualesDTO;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.repository.UserRepository;
import com.cruz_sur.api.service.IVentasService;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VentasService implements IVentasService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    @Override
    public List<VentasMensualesDTO> all() {
        Long usuarioId = getAuthenticatedUserId();
        String sql = "{CALL ObtenerVentasMensualesPorUsuario(?)}";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(VentasMensualesDTO.class), usuarioId);
    }

    @Override
    public ContarReservasDTO countTotalReservas(String fecha) {
        return executeReservasProcedure(fecha, "total_reservas");
    }

    @Override
    public List<ContarReservasDTO> countCamposReservados() {
        String sql = "{CALL ContarReservasDia(?, NULL, ?)}";
        Long usuarioId = getAuthenticatedUserId();
        return jdbcTemplate.query(sql, new Object[]{usuarioId, "campos_reservados"},
                new BeanPropertyRowMapper<>(ContarReservasDTO.class));
    }

    @Override
    public ContarReservasDTO countTotalReservasDiarias(String fecha) {
        return executeReservasProcedure(fecha, "total_reservas_diarias");
    }

    private ContarReservasDTO executeReservasProcedure(String fecha, String accion) {
        Long usuarioId = getAuthenticatedUserId();
        String sql = "{CALL ContarReservasDia(?, ?, ?)}";

        return jdbcTemplate.queryForObject(sql, new Object[]{usuarioId, fecha, accion}, (rs, rowNum) -> {
            ContarReservasDTO dto = new ContarReservasDTO();
            if ("total_reservas".equals(accion)) {
                dto.setTotalReservas(rs.getInt("TotalReservas"));
            } else if ("total_reservas_diarias".equals(accion)) {
                dto.setTotalReservas(rs.getInt("cantidad_reservas"));
                dto.setTotalMonetario(rs.getDouble("TotalReservasDiarias"));
            }
            return dto;
        });
    }

    private Long getAuthenticatedUserId() {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        if (authenticatedUser.getSede() == null) {
            throw new RuntimeException("User does not have a valid sede_id. Access denied.");
        }
        return authenticatedUser.getId();
    }
}
