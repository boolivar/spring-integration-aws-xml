package org.springframework.integration.aws.config.xml;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.integration.config.ExpressionFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.integration.aws.config.xml.ValueFactory.*;

class ValueFactoryTest {

    @Test
    void testTypedString() {
        assertThat(typedString().apply("val"))
            .isEqualTo(new TypedStringValue("val"));
    }

    @Test
    void testTypedTargetString() {
        assertThat(typedString(ValueFactoryTest.class).apply("test"))
            .isEqualTo(new TypedStringValue("test", ValueFactoryTest.class));
    }

    @Test
    void testExpressionBean() {
        var bean = expressionBean().apply("exp");

        assertThat(bean.getBeanClass())
            .isSameAs(ExpressionFactoryBean.class);
        assertThat(bean.getConstructorArgumentValues().getIndexedArgumentValues().values())
            .extracting(ValueHolder::getValue)
            .containsOnly("exp");
    }
}
