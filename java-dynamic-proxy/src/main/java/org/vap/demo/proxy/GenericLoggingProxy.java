package org.vap.demo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class GenericLoggingProxy implements InvocationHandler {
    private final Object delegate;

    private GenericLoggingProxy(Object delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
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
