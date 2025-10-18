package com.example.eventplanner.dto.event;

import com.example.eventplanner.model.event.Invitation;

import lombok.Getter;

@Getter
public class InvitationResult {
    private final Invitation invitation;
    private final InvitationErrorDto error;

    public InvitationResult(Invitation invitation) {
        this.invitation = invitation;
        this.error = null;
    }

    public InvitationResult(InvitationErrorDto error) {
        this.invitation = null;
        this.error = error;
    }

    public boolean isSuccess() {
        return invitation != null && error == null;
    }

}
