package org.vap.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.vap.demo.domain.Event;
import org.vap.demo.domain.Participant;
import org.vap.demo.proxy.AnnotatedLoggingProxy;
import org.vap.demo.proxy.GenericLoggingProxy;
import org.vap.demo.proxy.LoggingProxy;
import org.vap.demo.service.AdvancedRegistrationService;
import org.vap.demo.service.RegistrationService;
import org.vap.demo.service.SimpleRegistrationService;

import java.util.HashMap;
import java.util.Map;

public class AppTest {

    private Participant participant;
    private Event event;

    @Before
    public void setup() {
        participant = new Participant("Tester", "user@tester.com");
        event = new Event();
    }

    @Test
    public void proxyTest() {
        RegistrationService service = LoggingProxy.create(new SimpleRegistrationService());
        service.register(participant, event);
    }

    @Test
    public void genericProxyTest() {
        Map<String, String> proxyMap = GenericLoggingProxy.create(
                Map.class,
                new HashMap<>()
        );

        proxyMap.put("1", "one");
        proxyMap.put("2", "two");

        assertEquals("two", proxyMap.get("2"));
    }

    @Test
    public void annotatedProxyTest() {
        RegistrationService service = AnnotatedLoggingProxy.create(RegistrationService.class, new AdvancedRegistrationService());
        service.register(participant, event);

        assertTrue(service.isRegistered(participant, event));
    }
}
