package org.springframework.integration.aws.config.xml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.w3c.dom.Element;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class XmlBeanDefinitionBuilderTest {

    @Mock
    private Element element;

    @Mock
    private ParserContext parserContext;

    @Spy
    private final BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition();

    @InjectMocks
    private XmlBeanDefinitionBuilder builder;

    @Test
    void testProperties() {
        given(element.getAttribute("value-property"))
            .willReturn("value");
        given(element.getAttribute("ref-property"))
            .willReturn("bean");
        given(element.getAttribute("defined-property"))
            .willReturn("test");

        var def = builder
            .addConstructorArgValue("value-property")
            .addConstructorArgReference("ref-property")
            .setPropertyValue("value-property")
            .setPropertyValue("property", "value-property")
            .setPropertyReference("ref-property")
            .setPropertyReference("ref", "ref-property")
            .setPropertyValueIfAttributeDefined("defined-property")
            .setPropertyValueIfAttributeDefined("undefined-property")
            .setPropertyValueIfAttributeDefined("expected", "defined-property")
            .setPropertyValueIfAttributeDefined("unexpected", "undefined-property")
            .build();

        assertThat(def.getConstructorArgumentValues())
            .returns(2, ConstructorArgumentValues::getArgumentCount)
            .returns(new TypedStringValue("value"), ctor -> ctor.getIndexedArgumentValue(0, null).getValue())
            .returns(new RuntimeBeanReference("bean"), ctor -> ctor.getIndexedArgumentValue(1, null).getValue())
            ;

        assertThat(def.getPropertyValues())
            .containsOnly(
                new PropertyValue("valueProperty", new TypedStringValue("value")),
                new PropertyValue("property", new TypedStringValue("value")),
                new PropertyValue("refProperty", new RuntimeBeanReference("bean")),
                new PropertyValue("ref", new RuntimeBeanReference("bean")),
                new PropertyValue("definedProperty", new TypedStringValue("test")),
                new PropertyValue("expected", new TypedStringValue("test"))
            );
    }

    @Test
    void testExclusiveProperties() {
        given(element.getAttribute(any()))
            .willReturn(null);
        given(element.getAttribute("a-property"))
            .willReturn("a");
        given(element.getAttribute("b-property"))
            .willReturn("b");
        given(element.getAttribute("value"))
            .willReturn("c");
        given(element.getAttribute("test-expression"))
            .willReturn("d");

        var def = builder
            .setPropertyValueIfExclusiveAttributeDefined("a-property", "x-property")
            .setPropertyValueIfExclusiveAttributeDefined("y-property", "b-property")
            .setPropertyValueIfAttributeDefined("m-property", "n-property")
            .setPropertyValueIfExclusiveAttributeDefined("a-property", "x-property", "aExpectedProperty", "aUnexpectedProperty")
            .setPropertyValueIfExclusiveAttributeDefined("y-property", "b-property", "bUnexpectedProperty", "bExpectedProperty")
            .setPropertyValueIfExclusiveAttributeDefined("m-property", "n-property", "mUnexpectedProperty", "nUnexpectedProperty")
            .setExpressionValueIfAttributeDefined("nonexistent")
            .setExpressionValueIfAttributeDefined("value")
            .setExpressionValueIfAttributeDefined("test")
            .build();

        assertThat(def.getPropertyValues())
            .containsOnly(
                new PropertyValue("aProperty", new TypedStringValue("a")),
                new PropertyValue("bProperty", new TypedStringValue("b")),
                new PropertyValue("aExpectedProperty", new TypedStringValue("a")),
                new PropertyValue("bExpectedProperty", new TypedStringValue("b")),
                new PropertyValue("value", new TypedStringValue("c")),
                new PropertyValue("testExpressionString", new TypedStringValue("d"))
            );
    }

    @Test
    void testExclusivePropertiesError(@Mock XmlReaderContext readerContext) {
        given(parserContext.getReaderContext())
            .willReturn(readerContext);

        given(element.getAttribute("a-property"))
            .willReturn("a");
        given(element.getAttribute("b-property"))
            .willReturn("b");
        given(element.getAttribute("c-property"))
            .willReturn("c");
        given(element.getAttribute("d-property"))
            .willReturn("d");
        given(element.getAttribute("test"))
            .willReturn("e");
        given(element.getAttribute("test-expression"))
            .willReturn("f");

        var def = builder
                .setPropertyValueIfExclusiveAttributeDefined("a-property", "b-property")
                .setPropertyValueIfExclusiveAttributeDefined("c-property", "d-property", "c", "d")
                .setExpressionValueIfAttributeDefined("test")
                .build();

        assertThat(def.getPropertyValues())
            .isEmpty();
        then(readerContext).should()
            .error("a-property and b-property attributes are mutually exclusive", element);
        then(readerContext).should()
            .error("c-property and d-property attributes are mutually exclusive", element);
        then(readerContext).should()
            .error("test and test-expression attributes are mutually exclusive", element);
    }
}
