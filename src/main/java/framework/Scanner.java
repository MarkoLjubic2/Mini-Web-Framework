package framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Scanner {

    private static final String defaultPackageName = "test";

    public static List<Class<?>> scan() throws IOException {
        return scanPackage(defaultPackageName);
    }

    private static List<Class<?>> scanPackage(String packageName) throws IOException {
        String path = packageName.replace('.', '/');
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(path);

        if (stream == null) {
            throw new IOException("Could not get resource stream for package: " + packageName);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            List<Class<?>> classes = new ArrayList<>();
            List<String> lines = reader.lines().collect(Collectors.toList());

            for (String line : lines) {
                if (line.endsWith(".class")) {
                    Class<?> clazz = getClass(packageName, line);
                    if (clazz != null) {
                        classes.add(clazz);
                    }
                } else if (!line.contains(".")) {
                    classes.addAll(scanPackage(packageName + "." + line));
                }
            }

            return classes;
        }
    }

    private static Class<?> getClass(String packageName, String className) {
        try {
            String fullClassName = packageName + "." + className.substring(0, className.lastIndexOf('.'));
            return Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}