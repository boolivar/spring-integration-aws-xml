package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.inbound.SnsInboundChannelAdapter;
import org.springframework.integration.config.xml.AbstractChannelAdapterParser;
import org.w3c.dom.Element;

public class SnsInboundChannelAdapterParser extends AbstractChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition doParse(Element element, ParserContext parserContext, String channelName) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, SnsInboundChannelAdapter.class)
            .addConstructorArgReference("sns")
            .addConstructorArgValue("path")
            .setProperty("handle-notification-status")
            .setPropertyIfAttributeDefined("send-timeout", "requestTimeout")
            .setPropertyIfAttributeDefined("error-channel", "errorChannelName")
            .setPropertyIfAttributeDefined("payload-expression", "payloadExpression", ValueFactory.expressionBean())
            .build();
    }
}
