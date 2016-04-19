package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class Injector {
    private static List<Class<?>> visited = new ArrayList<>();
    private static Map<Class<?>, Object> instances = new HashMap<>();

    private Injector() {
    }

    private static Object getDependencyInstance(Class<?> dependencyClass, List<Class<?>> implementationClasses)
            throws
            InjectionCycleException, ImplementationNotFoundException, AmbiguousImplementationException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        if (instances.containsKey(dependencyClass)) {
            return instances.get(dependencyClass);
        }

        if (visited.contains(dependencyClass)) {
            throw new InjectionCycleException();
        }

        visited.add(dependencyClass);

        Constructor<?> constr = dependencyClass.getConstructors()[0];
        Class<?>[] parameterTypes = constr.getParameterTypes();

        if (parameterTypes.length < 1) {
            Object instance = constr.newInstance();
            instances.put(dependencyClass, instance);
            return instance;
        }

        List<Class<?>> concreteParameterTypes = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            Class<?> concreteParameterType = null;

            for (int i = 0; i < implementationClasses.size(); i++) {
                if (parameterType.isAssignableFrom(implementationClasses.get(i))) {
                    if (concreteParameterType != null) {
                        throw new AmbiguousImplementationException();
                    }
                    concreteParameterType = implementationClasses.get(i);
                }
            }

            if (concreteParameterType == null) {
                throw new ImplementationNotFoundException();
            }
            concreteParameterTypes.add(concreteParameterType);
        }

        List<Object> params = new ArrayList<>();
        for (Class<?> concreteParameterType : concreteParameterTypes) {
            params.add(getDependencyInstance(concreteParameterType, implementationClasses));
        }

        Object instance = constr.newInstance(params.toArray());

        instances.put(dependencyClass, instance);

        return instance;
    }

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        Class<?> rootClass = Class.forName(rootClassName);

        List<Class<?>> implementationClasses = new ArrayList<>();
        for (String className : implementationClassNames) {
            implementationClasses.add(Class.forName(className));
        }

        implementationClasses.add(rootClass);

        return getDependencyInstance(rootClass, implementationClasses);
    }
}
