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

We'd like to implement a logging proxy that can be applied to any class so that it is reusable.

```java
public class GenericLoggingProxy implements InvocationHandler {
    private final Object delegate;

    private GenericLoggingProxy(Object delegate) {
        this.delegate = delegate;
    }
    
    public static <T> T create(
            Class<T> interfaceType,	// any class of type T
            Object realObject) {	// an instance of type T

        return (T) Proxy.newProxyInstance(
                realObject.getClass().getClassLoader(),
                new Class[]{ interfaceType },
                new GenericLoggingProxy(realObject));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO implement the proxy logic here
        System.out.println("Invoking with proxy");
        return method.invoke(delegate, args);
    }
}
```

## Using Annotations in Generic Logging Proxy

The generic proxy invocation works for any method of the `realObject`. We would like to limit this in a way that the proxy works for certain methods of the `realObject` but not all.

This can be achieved using Annotations, by defining a `@Logged` annotation and having the proxy look for the existence of the `@Logged` annotation on the method and decide whether to execute the proxy logic or not.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) // Need to have it in runtime for the dynamic proxy
public @interface Logged {
}
```

Once the methods that we are interested have the annotation we can use the [Java Reflection API](https://docs.oracle.com/javase/tutorial/reflect/) to rely on them and execute the custom proxy login on the condition of existence of the annotation.

```java
public class AnnotatedLoggingProxy implements InvocationHandler {

    //...
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Must look up annotations feom the original object
        Method[] declaredMethods = delegate.getClass().getDeclaredMethods();

        // Look for the method which is currently being invoked
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().equals(method.getName())) {

                // See if we have the annotation we'd like on the current method
                if (declaredMethod.getAnnotation(Logged.class) != null) {
                    System.out.println("The annotated proxy works for " + method.getName() + " because it has the annotation");
                    // Any custom logic here
                }
            }
        }   
    }
}
```
