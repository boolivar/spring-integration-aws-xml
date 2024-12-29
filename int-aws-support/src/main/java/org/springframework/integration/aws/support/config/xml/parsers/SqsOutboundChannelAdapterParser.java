package org.springframework.integration.aws.support.config.xml.parsers;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.config.xml.ValueFactory;
import org.springframework.integration.aws.config.xml.XmlBeanDefinitionBuilder;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.integration.aws.support.config.xml.InvertedBooleanFactoryBean;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

public class SqsOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, SqsMessageHandler.class)
            .unsupportedAttributeWarning("failure-channel", "resource-id-resolver", "error-message-strategy", "async-handler")
            .addConstructorArgReference("sqs")
            .setPropertyOrExpressionStringIfAttributeDefined("queue")
            .setPropertyOrExpressionStringIfAttributeDefined("delay")
            .setPropertyOrExpressionStringIfAttributeDefined("message-group-id")
            .setPropertyOrExpressionStringIfAttributeDefined("message-deduplication-id")
            .setPropertyOrExpressionStringIfAttributeDefined("send-timeout")
            .setPropertyReferenceIfAttributeDefined("message-converter")
            .setPropertyIfAttributeDefined("success-channel", "outputChannelName")
            .setPropertyIfAttributeDefined("sync", "async", ValueFactory.bean(InvertedBooleanFactoryBean.class))
            .build();
    }
}
