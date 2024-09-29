package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

public class SpringIntegrationAwsNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        var classLoader = getClass().getClassLoader();
        var packageName = getClass().getPackageName();
        try (var in = classLoader.getResourceAsStream(packageName.replace('.', '/'))) {
            new BufferedReader(new InputStreamReader(in)).lines()
                .filter(name -> name.endsWith(".class"))
                .map(className -> loadClass(classLoader, packageName + '.' + className.replace(".class", "")))
                .filter(BeanDefinitionParser.class::isAssignableFrom)
                .forEach(type -> registerBeanDefinitionParser(mapName(type), createParser(type)));
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
            throw new RuntimeException("Error instantiate parser of type " + type);
        }
    }
}
