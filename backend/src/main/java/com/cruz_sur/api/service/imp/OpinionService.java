package com.cruz_sur.api.service.imp;

import com.cruz_sur.api.dto.OpinionDTO;
import com.cruz_sur.api.dto.OpinionSummaryDTO;
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
                .orElseThrow(() -> new RuntimeException("Opinion not found"));

        opinion.setEstado(status == 1 ? '1' : '0');
        Opinion updatedOpinion = opinionRepository.save(opinion);
        return convertToDTO(updatedOpinion);
    }

    @Override
    public OpinionDTO updateOpinion(Long id, OpinionDTO opinionDTO) {
        Opinion opinion = opinionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opinion not found with id: " + id));

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + authenticatedUsername));

        // Verificar que el usuario autenticado es el propietario de la opinión
        if (!opinion.getUser().getUsername().equals(authenticatedUsername)) {
            throw new RuntimeException("No tienes permiso para editar esta opinión.");
        }

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

    @Override
    public OpinionSummaryDTO summaryOpinion(Long companiaId) {
        List<Opinion> opiniones = opinionRepository.findByCompaniaId(companiaId);
        int star1Count = 0;
        int star2Count = 0;
        int star3Count = 0;
        int star4Count = 0;
        int star5Count = 0;

        for (Opinion opinion : opiniones) {
            switch (opinion.getCalificacion()) {
                case 1:
                    star1Count++;
                    break;
                case 2:
                    star2Count++;
                    break;
                case 3:
                    star3Count++;
                    break;
                case 4:
                    star4Count++;
                    break;
                case 5:
                    star5Count++;
                    break;
            }
        }
        double averageRating = opiniones.stream()
                .mapToInt(Opinion::getCalificacion)
                .average()
                .orElse(0.0);
        OpinionSummaryDTO summary = new OpinionSummaryDTO();
        summary.setCompaniaId(companiaId);
        summary.setStar1Count(star1Count);
        summary.setStar2Count(star2Count);
        summary.setStar3Count(star3Count);
        summary.setStar4Count(star4Count);
        summary.setStar5Count(star5Count);
        summary.setAverageRating(averageRating);
        summary.setTotalReviews(opiniones.size());

        return summary;
    }

}
