package framework;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Scanner {

    private static final String defaultPackageName = "test";

    public static List<Class<?>> scan() throws IOException {
        String path = defaultPackageName.replace('.', '/');
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(path);

        if (stream == null) {
            throw new IOException("Could not get resource stream for package: " + defaultPackageName);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines()
                    .filter(line -> line.endsWith(".class"))
                    .map(Scanner::getClass)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    private static Class<?> getClass(String className) {
        try {
            String fullClassName = defaultPackageName + "." + className.substring(0, className.lastIndexOf('.'));
            return Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
