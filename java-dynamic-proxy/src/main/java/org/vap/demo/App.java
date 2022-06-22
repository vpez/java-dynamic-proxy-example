package org.vap.demo;

import org.vap.demo.domain.Event;
import org.vap.demo.domain.Participant;
import org.vap.demo.factory.ParticipantFactory;
import org.vap.demo.proxy.LoggingProxy;
import org.vap.demo.service.LoggingRegistrationService;
import org.vap.demo.service.RegistrationService;
import org.vap.demo.service.SimpleRegistrationService;

import java.util.List;

public class App {
    public static void main(String[] args) {
        // Some test data
        ParticipantFactory factory = new ParticipantFactory();
        List<Participant> participants = factory.create(10);

        Event event = new Event();
        event.setName("Java Workshop");

        RegistrationService baseService = new SimpleRegistrationService();
//        RegistrationService service = new LoggingRegistrationService(baseService);

        RegistrationService service = LoggingProxy.create(baseService);

        for (Participant participant : participants) {
            service.register(participant, event);
        }
    }
}
