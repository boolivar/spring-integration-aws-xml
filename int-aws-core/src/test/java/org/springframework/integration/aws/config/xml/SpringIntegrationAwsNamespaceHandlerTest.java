package org.springframework.integration.aws.config.xml;

import org.assertj.core.api.Condition;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpringIntegrationAwsNamespaceHandlerTest {

    private final SpringIntegrationAwsNamespaceHandler handler = new SpringIntegrationAwsNamespaceHandler();

    @Test
    void test() {
        handler.init();
        assertThat(handler)
            .extracting("parsers", InstanceOfAssertFactories.MAP)
            .hasEntrySatisfying("alpha", instanceOf(AlphaTestBeanDefinitionParser.class))
            .hasEntrySatisfying("beta", instanceOf(BetaTestBeanDefinitionParser.class))
            ;
    }

    private Condition<Object> instanceOf(Class<?> type) {
        return new Condition<>(type::isInstance, "instance of " + type);
    }
}
