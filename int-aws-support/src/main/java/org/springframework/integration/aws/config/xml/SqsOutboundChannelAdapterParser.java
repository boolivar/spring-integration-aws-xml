package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

import java.util.List;

public class SqsOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        var builder = XmlBeanDefinitionBuilder.newInstance(element, parserContext, SqsMessageHandler.class)
            .addConstructorArgReference("sqs")
            .setExpressionValueIfAttributeDefined("queue")
            .setExpressionValueIfAttributeDefined("delay")
            .setExpressionValueIfAttributeDefined("message-group-id")
            .setExpressionValueIfAttributeDefined("message-deduplication-id")
            .setExpressionValueIfAttributeDefined("send-timeout")
            .setPropertyReferenceIfAttributeDefined("message-converter")
            .setPropertyReferenceIfAttributeDefined("outputChannelName", "success-channel")
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

        for (var attribute : List.of("failure-channel", "resource-id-resolver", "error-message-strategy", "async-handler")) {
            if (element.hasAttribute(attribute)) {
                builder.warning("Attribute " + attribute + " not supported");
            }
        }

        return builder.build();
    }
}
