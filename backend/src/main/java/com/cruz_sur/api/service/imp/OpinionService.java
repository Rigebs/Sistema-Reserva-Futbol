package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.OpinionDTO;
import com.cruz_sur.api.model.Compania;
import com.cruz_sur.api.model.Opinion;
import com.cruz_sur.api.model.User;
import com.cruz_sur.api.repository.CompaniaRepository;
import com.cruz_sur.api.repository.OpinionRepository;
import com.cruz_sur.api.repository.UserRepository;
import com.cruz_sur.api.service.IOpinionService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class OpinionService implements IOpinionService {

    private final OpinionRepository opinionRepository;
    private final UserRepository userRepository;
    private final CompaniaRepository companiaRepository;

    @Override
    public OpinionDTO saveOpinion(OpinionDTO opinionDTO) {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + authenticatedUsername));

        if (authenticatedUser.getCliente() == null || authenticatedUser.getSede() != null) {
            throw new RuntimeException("Solo los usuarios con cliente_id y sin sede_id pueden dejar opiniones.");
        }

        Compania compania = companiaRepository.findById(opinionDTO.getCompaniaId())
                .orElseThrow(() -> new RuntimeException("Compania not found with id: " + opinionDTO.getCompaniaId()));

        Opinion opinion = new Opinion();
        opinion.setContenido(opinionDTO.getContenido());
        opinion.setCalificacion(opinionDTO.getCalificacion());
        opinion.setUser(authenticatedUser);
        opinion.setCompania(compania);
        opinion.setUsuarioCreacion(authenticatedUsername);
        opinion.setFechaCreacion(LocalDateTime.now());

        Opinion savedOpinion = opinionRepository.save(opinion);

        return convertToDTO(savedOpinion);
    }

    @Override
    public List<OpinionDTO> getOpinionsByCompania(Long companiaId) {
        List<Opinion> opiniones = opinionRepository.findByCompaniaId(companiaId);
        return opiniones.stream().map(this::convertToDTO).toList();
    }



    @Override
    public List<OpinionDTO> getOpinionsByAuthenticatedUser() {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + authenticatedUsername));

        List<Opinion> opiniones = opinionRepository.findByUserId(authenticatedUser.getId());
        return opiniones.stream().map(this::convertToDTO).toList();
    }

    @Override
    public OpinionDTO changeStatus(Long id, Integer status) {
        Opinion opinion = opinionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        opinion.setEstado(status == 1 ? '1' : '0');
        Opinion updatedOpinion = opinionRepository.save(opinion);
        return convertToDTO(updatedOpinion);
    }

    @Override
    public OpinionDTO updateOpinion(OpinionDTO opinionDTO) {
        Opinion opinion = opinionRepository.findById(opinionDTO.getId())
                .orElseThrow(() -> new RuntimeException("Opinion not found with id: " + opinionDTO.getId()));

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + authenticatedUsername));

        Compania compania = companiaRepository.findById(opinionDTO.getCompaniaId())
                .orElseThrow(() -> new RuntimeException("Compania not found with id: " + opinionDTO.getCompaniaId()));

        opinion.setContenido(opinionDTO.getContenido());
        opinion.setCalificacion(opinionDTO.getCalificacion());
        opinion.setUsuarioModificacion(authenticatedUsername);
        opinion.setFechaModificacion(LocalDateTime.now());
        opinion.setUser(authenticatedUser);
        opinion.setCompania(compania);

        opinion = opinionRepository.save(opinion);
        return convertToDTO(opinion);
    }

    // Método de conversión
    private OpinionDTO convertToDTO(Opinion opinion) {
        return OpinionDTO.builder()
                .id(opinion.getId())
                .contenido(opinion.getContenido())
                .calificacion(opinion.getCalificacion())
                .usuarioCreacion(opinion.getUsuarioCreacion())
                .userId(opinion.getUser().getId())
                .companiaId(opinion.getCompania().getId())
                .build();
    }
}
