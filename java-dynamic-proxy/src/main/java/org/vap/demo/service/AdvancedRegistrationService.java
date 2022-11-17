package org.vap.demo.service;

import org.vap.demo.annotation.Logged;
import org.vap.demo.domain.Event;
import org.vap.demo.domain.Participant;

public class AdvancedRegistrationService extends SimpleRegistrationService {

    @Logged
    @Override
    public boolean register(Participant participant, Event event) {
        return super.register(participant, event);
    }

    @Override
    public boolean isRegistered(Participant participant, Event event) {
        return event.getParticipants().contains(participant);
    }
}
