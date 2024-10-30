package framework.di;

import framework.Scanner;
import framework.annotations.Qualifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DependencyContainer {

    private final Map<String, Class<?>> implementations = new HashMap<>();

    public DependencyContainer() throws IOException {
            setValues(Scanner.scan());
    }

    public void setValues(List<Class<?>> classes) {
        Map<String, Class<?>> qualifiedClasses = classes.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Qualifier.class))
                .collect(Collectors.toMap(
                        clazz -> clazz.getAnnotation(Qualifier.class).value(),
                        clazz -> clazz,
                        (existing, replacement) -> {
                            throw new RuntimeException("There are multiple @Bean-s with @Qualifier of the same value!");
                        }
                ));
        implementations.putAll(qualifiedClasses);
    }

    public Class<?> getImplementation(String name) {
        System.out.println("Implementations: " + implementations);
        return implementations.computeIfAbsent(name, key -> {
            throw new RuntimeException("@Qualifier with value " + key + " not found!");
        });
    }
}