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
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (authenticatedUser.getSede() == null) {
            throw new RuntimeException("User does not have a valid sede_id. Access denied.");
        }

        String sql = "{CALL ObtenerVentasMensualesPorUsuario(?)}";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(VentasMensualesDTO.class), authenticatedUser.getId());
    }

    @Override
    public ContarReservasDTO allDay(String fecha) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        if (authenticatedUser.getSede() == null) {
            throw new RuntimeException("User does not have a valid sede_id. Access denied.");
        }
        Long usuarioId = authenticatedUser.getId();
        String sql = "{CALL ContarReservasDia(?, ?)}";
        return jdbcTemplate.queryForObject(sql, new Object[]{usuarioId, fecha}, (rs, rowNum) -> {
            ContarReservasDTO dto = new ContarReservasDTO();
            dto.setTotalReservas(rs.getInt("TotalReservas"));
            return dto;
        });
    }


}
