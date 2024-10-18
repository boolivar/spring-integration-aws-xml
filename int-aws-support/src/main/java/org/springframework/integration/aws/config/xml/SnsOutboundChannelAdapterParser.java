package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.outbound.SnsMessageHandler;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

public class SnsOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, SnsMessageHandler.class)
            .unsupportedAttributeWarning("failure-channel", "resource-id-resolver", "error-message-strategy", "async-handler")
            .addConstructorArgReference("sns")
            .setExpressionPropertyIfAttributeDefined("topic-arn")
            .setExpressionPropertyIfAttributeDefined("subject")
            .setExpressionPropertyIfAttributeDefined("message-group-id")
            .setExpressionPropertyIfAttributeDefined("message-deduplication-id")
            .setExpressionPropertyIfAttributeDefined("body")
            .setExpressionValueIfAttributeDefined("send-timeout")
            .setPropertyValueIfAttributeDefined("outputChannelName", "success-channel")
            .build();
    }
}
