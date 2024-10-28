package framework;

import framework.annotations.Controller;
import framework.annotations.GET;
import framework.annotations.POST;
import framework.annotations.Path;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class Discovery {

    private final Map<String, Route> routes = new HashMap<>();

    public Discovery() {
        try {
            Set<Class<?>> controllers = findControllers();
            registerRoutes(controllers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<Class<?>> findControllers() throws IOException {
        List<Class<?>> classes = Scanner.scan();
        return classes.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
                .collect(Collectors.toSet());
    }

    private void registerRoutes(Set<Class<?>> controllers) {
        controllers.forEach(controller ->
                Arrays.stream(controller.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(Path.class))
                        .forEach(method -> registerRoute(controller, method))
        );
        System.out.println("Routes: " + routes);
    }

    private void registerRoute(Class<?> clazz, Method method) {
        Path pathAnnotation = method.getAnnotation(Path.class);
        String path = pathAnnotation.path();
        String httpMethod = getHttpMethod(method);

        if (httpMethod != null) {
            String routeKey = httpMethod + " " + path;
            if (routes.containsKey(routeKey)) {
                throw new RuntimeException("Duplicate route: " + routeKey);
            }
            routes.put(routeKey, new Route(clazz, method));
        }
    }

    private String getHttpMethod(Method method) {
        return method.isAnnotationPresent(GET.class) ? "GET" :
                method.isAnnotationPresent(POST.class) ? "POST" : null;
    }

    public Route getRoute(String httpMethod, String path) {
        return routes.get(httpMethod + " " + path);
    }

    public static class Route {
        private final Class<?> clazz;
        private final Method method;

        public Route(Class<?> controllerClass, Method method) {
            this.clazz = controllerClass;
            this.method = method;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public Method getMethod() {
            return method;
        }

        @Override
        public String toString() {
            return "class=" + clazz.getSimpleName() + ", method=" + method.getName();
        }
    }
}