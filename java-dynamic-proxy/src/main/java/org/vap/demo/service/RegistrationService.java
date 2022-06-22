package org.vap.demo.service;

import org.vap.demo.domain.Event;
import org.vap.demo.domain.Participant;

public interface RegistrationService {
    boolean register(Participant participant, Event event);
}
