package org.springframework.integration.aws.support.config.xml.parsers;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.config.xml.XmlBeanDefinitionBuilder;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

public class SqsOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        var builder = XmlBeanDefinitionBuilder.newInstance(element, parserContext, SqsMessageHandler.class)
            .unsupportedAttributeWarning("failure-channel", "resource-id-resolver", "error-message-strategy", "async-handler")
            .addConstructorArgReference("sqs")
            .setPropertyOrExpressionStringIfAttributeDefined("queue")
            .setPropertyOrExpressionStringIfAttributeDefined("delay")
            .setPropertyOrExpressionStringIfAttributeDefined("message-group-id")
            .setPropertyOrExpressionStringIfAttributeDefined("message-deduplication-id")
            .setPropertyOrExpressionStringIfAttributeDefined("send-timeout")
            .setPropertyReferenceIfAttributeDefined("message-converter")
            .setPropertyIfAttributeDefined("success-channel", "outputChannelName")
            ;

        if (element.hasAttribute("sync")) {
            var sync = element.getAttribute("sync");
            if ("true".equalsIgnoreCase(sync)) {
                builder.getBeanDefinitionBuilder().addPropertyValue("async", new TypedStringValue("false"));
            } else if ("false".equalsIgnoreCase(sync)) {
                builder.getBeanDefinitionBuilder().addPropertyValue("async", new TypedStringValue("true"));
            } else {
                builder.error("expected boolean for 'sync' attribute");
            }
        }

        return builder.build();
    }
}
