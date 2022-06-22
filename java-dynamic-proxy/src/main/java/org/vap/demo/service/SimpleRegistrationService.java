package org.vap.demo.service;

import org.vap.demo.domain.Event;
import org.vap.demo.domain.Participant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleRegistrationService implements RegistrationService {
    @Override
    public boolean register(Participant participant, Event event) {
        if (event.getParticipants() == null) {
            event.setParticipants(new ArrayList<>());
        }

        if (!event.getParticipants().contains(participant)) {
            event.getParticipants().add(participant);
            return true;
        }

        return false;
    }
}
