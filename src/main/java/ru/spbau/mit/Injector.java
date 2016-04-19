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

        Class<?> parameterType = parameterTypes[0];

        int idx = 0;
        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < implementationClasses.size(); i++) {
            if (parameterType.isAssignableFrom(implementationClasses.get(i))) {
                candidates.add(i);
            }
        }

        if (candidates.size() < 1) {
            throw new ImplementationNotFoundException();
        } else if (candidates.size() > 1) {
            throw new AmbiguousImplementationException();
        }

        Class<?> nextDep = implementationClasses.get(candidates.get(0));

        Object instance = constr.newInstance(getDependencyInstance(nextDep, implementationClasses));

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

        return getDependencyInstance(rootClass, implementationClasses);
    }
}
