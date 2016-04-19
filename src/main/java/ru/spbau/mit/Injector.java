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

        List<Integer> candidates = new ArrayList<>();
        for (int k = 0; k < parameterTypes.length; k++) {
            List<Integer> localCandidates = new ArrayList<>();
            for (int i = 0; i < implementationClasses.size(); i++) {
                if (parameterTypes[k].isAssignableFrom(implementationClasses.get(i))) {
                    localCandidates.add(i);
                }
            }

            if (localCandidates.size() < 1) {
                throw new ImplementationNotFoundException();
            } else if (localCandidates.size() > 1) {
                throw new AmbiguousImplementationException();
            } else {
                candidates.add(localCandidates.get(0));
            }
        }

        List<Object> params = new ArrayList<>();
        for (Integer idx : candidates) {
            params.add(getDependencyInstance(implementationClasses.get(idx), implementationClasses));
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
