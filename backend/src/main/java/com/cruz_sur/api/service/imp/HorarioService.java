package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Horario;
import com.cruz_sur.api.repository.HorarioRepository;
import com.cruz_sur.api.service.IHorarioService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HorarioService implements IHorarioService {

    private final HorarioRepository horarioRepository;

    @Transactional
    @Override
    public Horario save(Horario horario) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        horario.setUsuarioCreacion(authenticatedUsername);
        horario.setFechaCreacion(LocalDateTime.now());
        horario.setEstado('1'); // Set default status to active

        return horarioRepository.save(horario);
    }

    @Transactional
    @Override
    public Horario update(Long id, Horario horario) {
        Horario horarioExistente = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        horarioExistente.setHoraInicio(horario.getHoraInicio());
        horarioExistente.setHoraFinal(horario.getHoraFinal());

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        horarioExistente.setUsuarioModificacion(authenticatedUsername);
        horarioExistente.setFechaModificacion(LocalDateTime.now());

        return horarioRepository.save(horarioExistente);
    }

    @Override
    public List<Horario> all() {
        return horarioRepository.findAll();
    }

    @Transactional
    @Override
    public Horario changeStatus(Long id, Integer status) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        horario.setEstado(status == 1 ? '1' : '0');

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        horario.setUsuarioModificacion(authenticatedUsername);
        horario.setFechaModificacion(LocalDateTime.now());

        return horarioRepository.save(horario);
    }

    @Override
    public Optional<Horario> byId(Long id) {
        return horarioRepository.findById(id);
    }
}
