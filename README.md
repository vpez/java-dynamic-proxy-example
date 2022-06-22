# java-dynamic-proxy-example

## Problem

You are developing a service that registers participants in events. You are interested in providing additional features to your service, such as logging, monitoring, and authentication.

## Basics

```java
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Participant {
    private String name;
    private String email;
}

@Getter
@Setter
public class Event {
    private String name;
    private List<Participant> participants;
}
```

```java
public interface RegistrationService {
    boolean register(Participant participant, Event event);
}

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
```

## Decorator Pattern

We are adding time-based logging in order to measure the latency of registering participants in an event. We do not modify the main source code of the service implementation, but rather use the **decorator pattern**.

```java
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

```

Usage in application:

```java
    public static void main(String[] args) {
        // Some test data
        ParticipantFactory factory = new ParticipantFactory();
        List<Participant> participants = factory.create(10);

        Event event = new Event();
        event.setName("Java Workshop");

        RegistrationService service = new LoggingRegistrationService(new SimpleRegistrationService());

        for (Participant participant : participants) {
            service.register(participant, event);
        }
    }
```

## Dynamic Proxy

The decorator pattern performs well, but the issue is that we need to implement a new class for that purpose. Instead, we'd like to separate the service implementation code from the logging code:

```java
public class LoggingProxy {
    public static RegistrationService create(RegistrationService delegate) {
        return (RegistrationService) Proxy.newProxyInstance(
                delegate.getClass().getClassLoader(),
                new Class[]{RegistrationService.class},
                (proxy, method, args) -> {
                    long start = System.nanoTime();
                    Object result = method.invoke(delegate, args);
                    long finish = System.nanoTime();
                    System.out.println("register() running time: " + (finish - start) / 1000 + " µs");
                    return result;
                });
    }
}
```

Usage in application:
```java
    public static void main(String[] args) {
        // Some test data
        ParticipantFactory factory = new ParticipantFactory();
        List<Participant> participants = factory.create(10);

        Event event = new Event();
        event.setName("Java Workshop");

        RegistrationService service = LoggingProxy.create(new SimpleRegistrationService());

        for (Participant participant : participants) {
            service.register(participant, event);
        }
    }
```

## Generic Logging Proxy

We'd like to implememnt a logging proxy that can be applied to any class so that it is reusable.
