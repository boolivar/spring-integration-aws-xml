package org.springframework.integration.aws.config.xml.parsers;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.config.xml.XmlBeanDefinitionBuilder;
import org.springframework.integration.aws.outbound.KplMessageHandler;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

import java.time.Duration;

public class KplOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, KplMessageHandler.class)
            .addConstructorArgReference("kinesis-producer")
            .setPropertyIfAttributeDefined("async")
            .setPropertyIfAttributeDefined("output-channel", "outputChannelName")
            .setPropertyIfAttributeDefined("send-timeout")
            .setPropertyOrExpressionStringIfAttributeDefined("stream")
            .setPropertyOrExpressionStringIfAttributeDefined("partition-key")
            .setPropertyOrExpressionStringIfAttributeDefined("explicit-hash-key")
            .setPropertyIfAttributeDefined("sequence-number-expression", "sequenceNumberExpressionString")
            .setPropertyIfAttributeDefined("glue-schema-expression", "glueSchemaExpressionString")
            .setPropertyIfAttributeDefined("flush-duration", "flushDuration", this::duration)
            .setPropertyReferenceIfAttributeDefined("embedded-headers-mapper")
            .setPropertyReferenceIfAttributeDefined("message-converter")
            .build();
    }

    private AbstractBeanDefinition duration(String millis) {
        return BeanDefinitionBuilder.genericBeanDefinition(Duration.class)
            .addConstructorArgValue(millis)
            .setFactoryMethod("ofMillis")
            .applyCustomizers(def -> def.setAutowireCandidate(false))
            .getBeanDefinition();
    }
}
