package org.springframework.integration.junit;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MockitoInlineExtension implements ParameterResolver, BeforeTestExecutionCallback {

    private final Namespace namespace = Namespace.create(MockitoInlineExtension.class);

    private final Map<Class<?>, ExtensionHandler> handlers;

    MockitoInlineExtension() {
        this(List.of(new ConstructionMockHandler(), new StaticMockHandler()));
    }

    MockitoInlineExtension(List<ExtensionHandler> handlers) {
        this(handlers.stream().collect(Collectors.toMap(ExtensionHandler::parameterType, h -> h)));
    }

    MockitoInlineExtension(Map<Class<?>, ExtensionHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        for (ExtensionHandler handler : handlers.values()) {
            var store = context.getStore(namespace.append(handler.parameterType()));
            handler.init(store, context.getTestClass(), context.getTestMethod(), context.getTestInstance());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getDeclaringExecutable().equals(extensionContext.getTestMethod().orElse(null))
            && handlers.keySet().contains(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        var parameter = parameterContext.getParameter();
        var store = extensionContext.getStore(namespace.append(parameter.getType()));
        return handlers.get(parameter.getType()).resolveParameter(store, parameter);
    }
}