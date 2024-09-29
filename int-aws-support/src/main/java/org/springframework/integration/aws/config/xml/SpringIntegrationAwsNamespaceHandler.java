package org.springframework.integration.aws.config.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpringIntegrationAwsNamespaceHandler extends NamespaceHandlerSupport {

    private final Logger log = LoggerFactory.getLogger(SpringIntegrationAwsNamespaceHandler.class);

    @Override
    public void init() {
        var classLoader = getClass().getClassLoader();
        var packageName = getClass().getPackageName();
        try {
            for (var paths = classLoader.getResources(packageName.replace('.', '/')); paths.hasMoreElements();) {
                var path = paths.nextElement();
                try {
                    Files.list(Path.of(path.toURI()))
                        .filter(Files::isRegularFile)
                        .map(file -> file.getFileName().toString())
                        .filter(fileName -> fileName.endsWith("Parser.class"))
                        .map(fileName -> loadClass(classLoader, packageName + '.' + fileName.replace(".class", "")))
                        .filter(BeanDefinitionParser.class::isAssignableFrom)
                        .forEach(type -> registerBeanDefinitionParser(mapName(type), createParser(type)))
                        ;
                } catch (URISyntaxException e) {
                    log.warn("Error reading path {}", path, e);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading classes for package " + packageName, e);
        }
    }

    private String mapName(Class<?> type) {
        var typeName = type.getSimpleName();
        var length = typeName.length() - "Parser".length();
        var sb = new StringBuilder();
        sb.append(Character.toLowerCase(typeName.charAt(0)));
        for (int i = 1; i < length; ++i) {
            var c = typeName.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('-').append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Class<?> loadClass(ClassLoader classLoader, String name) {
        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error load class " + name, e);
        }
    }

    private BeanDefinitionParser createParser(Class<?> type) {
        try {
            return (BeanDefinitionParser) type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error instantiate parser of type " + type, e);
        }
    }
}
