package org.springframework.integration.junit;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;
import org.mockito.MockedConstruction;
import org.mockito.MockedConstruction.Context;
import org.mockito.Mockito;

import java.lang.reflect.ParameterizedType;
import java.util.stream.Stream;

public class MockedConstructionExtension implements ParameterResolver, BeforeTestExecutionCallback {

    private final Namespace namespace = Namespace.create(MockedConstructionExtension.class);

    static class Mocked<T> implements CloseableResource {

        private final MockedConstruction<T> resource;

        Mocked(MockedConstruction<T> resource) {
            this.resource = resource;
        }

        public MockedConstruction<T> getResource() {
            return resource;
        }

        @Override
        public void close() {
            resource.close();
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getDeclaringExecutable().equals(extensionContext.getTestMethod().orElse(null))
            && parameterContext.getParameter().getType() == MockedConstruction.class;
    }

    @Override
    public MockedConstruction<?> resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        var typeArg = ((ParameterizedType) parameterContext.getParameter().getParameterizedType()).getActualTypeArguments()[0];
        var mocked = extensionContext.getStore(namespace).get(typeArg, Mocked.class);
        if (mocked == null) {
            throw new IllegalArgumentException("No MockedConstruction for " + typeArg + ". Add " + typeArg + " to @ConstructionMock");
        }
        return mocked.getResource();
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        context.getTestMethod()
            .flatMap(method -> AnnotationSupport.findAnnotation(method, ConstructionMock.class)).stream()
            .flatMap(annotation -> Stream.of(annotation.value()))
            .forEach(mockType -> context.getStore(namespace).put(mockType, new Mocked<>(mockConstruction(context, mockType))));
    }

    private <T> MockedConstruction<T> mockConstruction(ExtensionContext context, Class<T> mockType) {
        return ReflectionSupport.findMethod(context.getRequiredTestClass(), context.getRequiredTestMethod().getName(), mockType, Context.class)
            .map(method -> Mockito.mockConstruction(mockType, (m, c) -> ReflectionSupport.invokeMethod(method, context.getRequiredTestInstance(), m, c)))
            .orElseGet(() -> Mockito.mockConstruction(mockType));
    }
}
