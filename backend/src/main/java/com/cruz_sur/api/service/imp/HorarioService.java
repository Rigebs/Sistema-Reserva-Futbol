package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.model.Horario;
import com.cruz_sur.api.repository.HorarioRepository;
import com.cruz_sur.api.service.IHorarioService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HorarioService implements IHorarioService {

    private final HorarioRepository horarioRepository;

    @Override
    public Horario save(Horario horario) {
        return horarioRepository.save(horario);
    }

    @Override
    public Horario update(Long id, Horario horario) {
        Horario horarioExistente = horarioRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Horario no encontrado")
        );
        horarioExistente.setHoraInicio(horario.getHoraInicio());
        horarioExistente.setHoraFinal(horario.getHoraFinal());
        horarioExistente.setUsuarioModificacion(horario.getUsuarioModificacion());
        horarioExistente.setFechaModificacion(horario.getFechaModificacion());

        return horarioRepository.save(horarioExistente);
    }

    @Override
    public List<Horario> all() {
        return horarioRepository.findAll();
    }

    @Override
    public Horario changeStatus(Long id, Integer status) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        horario.setEstado(status == 1 ? '1' : '0');
        return horarioRepository.save(horario);
    }

    @Override
    public Optional<Horario> byId(Long id) {
        return horarioRepository.findById(id);
    }
}
