package org.springframework.integration.junit;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;
import org.mockito.MockedConstruction;
import org.mockito.MockedConstruction.Context;
import org.mockito.Mockito;

public class MockedConstructionExtension implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private final Namespace namespace = Namespace.create(MockedConstructionExtension.class);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return extensionContext.getTestMethod().isPresent()
            && parameterContext.getDeclaringExecutable().equals(extensionContext.getRequiredTestMethod())
            && extensionContext.getRequiredTestMethod().isAnnotationPresent(ConstructionMock.class)
            && parameterContext.getParameter().getType() == MockedConstruction.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return extensionContext.getStore(namespace).get("mocked");
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        var annotation = context.getTestMethod()
            .flatMap(method -> AnnotationSupport.findAnnotation(method, ConstructionMock.class)).orElse(null);
        if (annotation != null) {
            context.getStore(namespace).put("mocked", mockConstruction(context, annotation.value()));
        }
    }

    private <T> MockedConstruction<T> mockConstruction(ExtensionContext context, Class<T> mockType) {
        return ReflectionSupport.findMethod(context.getRequiredTestClass(), context.getRequiredTestMethod().getName(), mockType, Context.class)
            .map(method -> Mockito.mockConstruction(mockType, (m, c) -> ReflectionSupport.invokeMethod(method, context.getRequiredTestInstance(), m, c)))
            .orElseGet(() -> Mockito.mockConstruction(mockType));
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        var mocked = context.getStore(namespace).get("mocked");
        if (mocked != null) {
            ((AutoCloseable) mocked).close();
        }
    }
}
