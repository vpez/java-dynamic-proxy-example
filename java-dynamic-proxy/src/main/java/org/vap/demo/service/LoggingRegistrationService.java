package org.vap.demo.service;

import org.vap.demo.domain.Event;
import org.vap.demo.domain.Participant;

/**
 * This is an example of Proxy Pattern in Java. We are adding pieces of functionality on a class without modifying the source code of that class.
 * Instead, we include an instance of that class inside our Proxy Class and build our custom functionality around it.
 * @see <a href="https://refactoring.guru/design-patterns/proxy">Proxy Pattern</a>
 */
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

    @Override
    public boolean isRegistered(Participant participant, Event event) {
        return delegate.isRegistered(participant, event);
    }
}
