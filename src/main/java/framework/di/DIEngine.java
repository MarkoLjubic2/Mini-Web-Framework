package framework.di;

import framework.annotations.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

public class DIEngine {
    private final DependencyContainer container;
    private final Map<Class<?>, Object> singletonInstances = new HashMap<>();

    private static final List<Class<? extends Annotation>> annotations =
            Arrays.asList(Bean.class, Component.class, Service.class, Controller.class);

    public DIEngine() {
        try {
            this.container = new DependencyContainer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object initialize(Class<?> clazz) {
        if (annotations.stream().noneMatch(clazz::isAnnotationPresent)) {
            throw new RuntimeException("Class doesn't have required annotation!");
        }

        return singletonInstances.containsKey(clazz) ?
                singletonInstances.get(clazz) : createInstance(clazz);
    }

    private Object createInstance(Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            injectDependencies(instance);
            if (isSingleton(clazz)) singletonInstances.put(clazz, instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance for " + clazz, e);
        }
    }

    private void injectDependencies(Object instance) {
        Arrays.stream(instance.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .forEach(field -> {
                    try {
                        Object dependency = resolveDependency(field);
                        field.setAccessible(true);
                        field.set(instance, dependency);

                        System.out.println("Injected " + field.getName() + " in " + instance.getClass().getSimpleName());

                        logInjection(field, instance, dependency);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private Object resolveDependency(Field field) {
        Class<?> type = field.getType();
        if (type.isInterface()) {
            String qualifier = Optional.ofNullable(field.getAnnotation(Qualifier.class))
                    .map(Qualifier::value)
                    .orElseThrow(() -> new RuntimeException("Autowired Interface must have a Qualifier"));
            return initialize(container.getImplementation(qualifier));
        }
        return initialize(type);
    }

    private void logInjection(Field field, Object instance, Object dependency) {
        if (field.getAnnotation(Autowired.class).verbose()) {
            System.out.printf("Initialized %s %s in %s on %s with %d%n",
                    field.getType().getSimpleName(),
                    field.getName(),
                    instance.getClass().getSimpleName(),
                    LocalDateTime.now(),
                    dependency.hashCode());
        }
    }

    private boolean isSingleton(Class<?> clazz) {
        return clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Controller.class) ||
                (clazz.isAnnotationPresent(Bean.class) && clazz.getAnnotation(Bean.class).scope().equals(Scope.SINGLETON));
    }
}