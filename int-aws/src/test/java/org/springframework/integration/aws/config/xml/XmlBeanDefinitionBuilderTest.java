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
        given(element.hasAttribute("undefined-ref"))
            .willReturn(false);
        given(element.hasAttribute("ref-property"))
            .willReturn(true);
        given(element.getAttribute("value-property"))
            .willReturn("value");
        given(element.getAttribute("ref-property"))
            .willReturn("bean");
        given(element.getAttribute("defined-property"))
            .willReturn("test");

        var def = builder
            .addConstructorArgValue("value-property")
            .addConstructorArgReference("ref-property")
            .setPropertyReference("ref-property")
            .setPropertyReference("ref-property", "ref")
            .setPropertyReferenceIfAttributeDefined("undefined-ref")
            .setPropertyReferenceIfAttributeDefined("ref-property", "definedRef")
            .setPropertyReferenceIfAttributeDefined("undefinedRef", "undefined-ref")
            .setProperty("value-property")
            .setProperty("value-property", "property")
            .setPropertyIfAttributeDefined("defined-property")
            .setPropertyIfAttributeDefined("undefined-property")
            .setPropertyIfAttributeDefined("defined-property", "expected")
            .setPropertyIfAttributeDefined("undefined-property", "unexpected")
            .build();

        assertThat(def.getConstructorArgumentValues())
            .returns(2, ConstructorArgumentValues::getArgumentCount)
            .returns(new TypedStringValue("value"), ctor -> ctor.getIndexedArgumentValue(0, null).getValue())
            .returns(new RuntimeBeanReference("bean"), ctor -> ctor.getIndexedArgumentValue(1, null).getValue())
            ;

        assertThat(def.getPropertyValues())
            .containsOnly(
                new PropertyValue("refProperty", new RuntimeBeanReference("bean")),
                new PropertyValue("ref", new RuntimeBeanReference("bean")),
                new PropertyValue("definedRef", new RuntimeBeanReference("bean")),
                new PropertyValue("valueProperty", new TypedStringValue("value")),
                new PropertyValue("property", new TypedStringValue("value")),
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
        given(element.getAttribute("test-property"))
            .willReturn("c");
        given(element.getAttribute("test-expression"))
            .willReturn("d");

        var def = builder
            .setPropertyIfExclusiveAttributeDefined("a-property", "x-property")
            .setPropertyIfExclusiveAttributeDefined("y-property", "b-property")
            .setPropertyIfAttributeDefined("m-property", "n-property")
            .setPropertyIfExclusiveAttributeDefined("a-property", "x-property", "aExpectedProperty", "aUnexpectedProperty")
            .setPropertyIfExclusiveAttributeDefined("y-property", "b-property", "bUnexpectedProperty", "bExpectedProperty")
            .setPropertyIfExclusiveAttributeDefined("m-property", "n-property", "mUnexpectedProperty", "nUnexpectedProperty")
            .setPropertyOrExpressionIfAttributeDefined("nonexistent")
            .setPropertyOrExpressionIfAttributeDefined("test-property")
            .setPropertyOrExpressionIfAttributeDefined("test")
            .build();

        assertThat(def.getPropertyValues())
            .containsOnly(
                new PropertyValue("aProperty", new TypedStringValue("a")),
                new PropertyValue("bProperty", new TypedStringValue("b")),
                new PropertyValue("aExpectedProperty", new TypedStringValue("a")),
                new PropertyValue("bExpectedProperty", new TypedStringValue("b")),
                new PropertyValue("testProperty", new TypedStringValue("c")),
                new PropertyValue("testExpression", ValueFactory.expressionBean().apply("d"))
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
            .setPropertyIfExclusiveAttributeDefined("a-property", "b-property")
            .setPropertyIfExclusiveAttributeDefined("c-property", "d-property", "c", "d")
            .setPropertyOrExpressionIfAttributeDefined("test")
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
