package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

import static org.springframework.integration.config.xml.IntegrationNamespaceUtils.*;

public class SqsOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        var builder = BeanDefinitionBuilder.genericBeanDefinition(SqsMessageHandler.class)
            .addConstructorArgReference(element.getAttribute("sqs"));

        setIfAttributeDefined(element, parserContext, builder, "queue");
        setIfAttributeDefined(element, parserContext, builder, "delay");
        setIfAttributeDefined(element, parserContext, builder, "message-group-id");
        setIfAttributeDefined(element, parserContext, builder, "message-deduplication-id");

        return builder.getBeanDefinition();
    }
    
    private void setIfAttributeDefined(Element element, ParserContext parserContext, BeanDefinitionBuilder builder, String attribute) {
        if (element.hasAttribute(attribute) && element.hasAttribute(attribute + "-expression")) {
            parserContext.getReaderContext()
                .error(attribute + " and " + attribute + "-expression attributes are mutually exclusive", element);
        } else {
            setValueIfAttributeDefined(builder, element, attribute);
            setValueIfAttributeDefined(builder, element, attribute + "-expression", Conventions.attributeNameToPropertyName(attribute + "-expression-string"));
        }
    }
}
