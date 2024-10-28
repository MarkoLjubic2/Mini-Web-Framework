package framework.di;

import framework.Scanner;
import framework.annotations.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DIEngine {
    private final DependencyContainer container = new DependencyContainer();
    private final Map<Class<?>, Object> singletonInstances = new HashMap<>();

    public DIEngine() {
        try {
            List<Class<?>> classes = Scanner.scan();
            container.setValues(classes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T initialize(Class<T> clazz) throws Exception {
        if (!clazz.isAnnotationPresent(Bean.class) && !clazz.isAnnotationPresent(Component.class) &&
                !clazz.isAnnotationPresent(Service.class) && !clazz.isAnnotationPresent(Controller.class)) {
            throw new RuntimeException("Class doesn't have required annotation!");
        }
        if (singletonInstances.containsKey(clazz)) {
            return (T) singletonInstances.get(clazz);
        }
        T instance = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                Object dependency = resolveDependency(field);
                field.setAccessible(true);
                field.set(instance, dependency);

                if (autowired.verbose()) {
                    System.out.printf("Initialized %s %s in %s on %s with %d%n",
                            field.getType().getSimpleName(),
                            field.getName(),
                            clazz.getSimpleName(),
                            LocalDateTime.now(),
                            dependency.hashCode());
                }
            }
        }

        if (clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Controller.class) ||
                (clazz.isAnnotationPresent(Bean.class) && clazz.getAnnotation(Bean.class).scope().equals(Scope.SINGLETON))) {
            singletonInstances.put(clazz, instance);
        }

        return instance;
    }

    private Object resolveDependency(Field field) throws Exception {
        Class<?> type = field.getType();
        if (type.isInterface()) {
            if (!field.isAnnotationPresent(Qualifier.class)) {
                throw new RuntimeException("Autowired field of type Interface must have a Qualifier annotation");
            }
            Qualifier qualifier = field.getAnnotation(Qualifier.class);
            Class<?> implementationClass = container.getImplementation(qualifier.value());
            return initialize(implementationClass);
        } else {
            return initialize(type);
        }
    }
}