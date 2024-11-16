package com.cruz_sur.api.responses.event;

import com.cruz_sur.api.model.User;
import lombok.AllArgsConstructor;
import lombok.*;

@Data
@AllArgsConstructor
public class RoleUpdatedEvent {
    private final User user;
}