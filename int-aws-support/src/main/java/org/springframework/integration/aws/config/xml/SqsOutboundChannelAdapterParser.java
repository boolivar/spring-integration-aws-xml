package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

import java.util.List;

import static org.springframework.integration.config.xml.IntegrationNamespaceUtils.*;

public class SqsOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        var builder = BeanDefinitionBuilder.genericBeanDefinition(SqsMessageHandler.class)
            .addConstructorArgReference(element.getAttribute("sqs"));

        setExpressionValueIfAttributeDefined(builder, element, parserContext, "queue");
        setExpressionValueIfAttributeDefined(builder, element, parserContext, "delay");
        setExpressionValueIfAttributeDefined(builder, element, parserContext, "message-group-id");
        setExpressionValueIfAttributeDefined(builder, element, parserContext, "message-deduplication-id");
        setExpressionValueIfAttributeDefined(builder, element, parserContext, "send-timeout");

        setReferenceIfAttributeDefined(builder, element, "success-channel", "outputChannelName");

        if (element.hasAttribute("sync")) {
            var sync = element.getAttribute("sync");
            if ("true".equalsIgnoreCase(sync)) {
                builder.addPropertyValue("async", "false");
            } else if ("false".equalsIgnoreCase(sync)) {
                builder.addPropertyValue("async", "true");
            } else {
                parserContext.getReaderContext()
                    .error("expected boolean for sync attribute", element);
            }
        }

        for (var attribute : List.of("failure-channel", "resource-id-resolver", "error-message-strategy", "async-handler")) {
            if (element.hasAttribute(attribute)) {
                parserContext.getReaderContext()
                    .warning("Attribute" + attribute + " not supported", element);
            }
        }

        return builder.getBeanDefinition();
    }

    private void setExpressionValueIfAttributeDefined(BeanDefinitionBuilder builder, Element element, ParserContext parserContext, String attribute) {
        if (element.hasAttribute(attribute) && element.hasAttribute(attribute + "-expression")) {
            parserContext.getReaderContext()
                .error(attribute + " and " + attribute + "-expression attributes are mutually exclusive", element);
        } else {
            setValueIfAttributeDefined(builder, element, attribute);
            setValueIfAttributeDefined(builder, element, attribute + "-expression", Conventions.attributeNameToPropertyName(attribute + "-expression-string"));
        }
    }
}
