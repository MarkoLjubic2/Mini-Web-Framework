package framework.di;

import framework.annotations.Qualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DependencyContainer {

    private final Map<String, Class<?>> data = new HashMap<>();

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
        data.putAll(qualifiedClasses);
    }

    public Class<?> getImplementation(String name) {
        return data.computeIfAbsent(name, key -> {
            throw new RuntimeException("@Qualifier with value " + key + " not found!");
        });
    }
}