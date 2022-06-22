package org.vap.demo.service;

import org.vap.demo.domain.Event;
import org.vap.demo.domain.Participant;

public class LoggingRegistrationService implements RegistrationService {

    private final RegistrationService delegate;

    public LoggingRegistrationService(RegistrationService delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean register(Participant participant, Event event) {
        long start = System.nanoTime();
        boolean response = delegate.register(participant, event);
        long finish = System.nanoTime();
        System.out.println("register() running time: " + (finish - start) / 1000 + " µs");
        return response;
    }
}
