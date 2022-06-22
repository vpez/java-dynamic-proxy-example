package org.vap.demo.proxy;

import org.vap.demo.service.RegistrationService;

import java.lang.reflect.Proxy;

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
