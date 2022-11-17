package org.vap.demo.proxy;

import org.vap.demo.annotation.Logged;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AnnotatedLoggingProxy implements InvocationHandler {
    private final Object delegate;

    private AnnotatedLoggingProxy(Object delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(
            Class<T> interfaceType,	// any class of type T
            Object realObject) {	// an instance of type T

        return (T) Proxy.newProxyInstance(
                realObject.getClass().getClassLoader(),
                new Class[]{ interfaceType },
                new AnnotatedLoggingProxy(realObject));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Method[] declaredMethods = delegate.getClass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().equals(method.getName())) {
                if (declaredMethod.getAnnotation(Logged.class) != null) {
                    System.out.println("The annotated proxy works for " + method.getName() + " because it has the annotation");
                    // Any custom logic here
                }
            }
        }

        return method.invoke(delegate, args);
    }
}
