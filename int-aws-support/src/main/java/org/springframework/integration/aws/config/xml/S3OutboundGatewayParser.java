package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractConsumerEndpointParser;
import org.w3c.dom.Element;

public class S3OutboundGatewayParser extends AbstractConsumerEndpointParser {

    @Override
    protected String getInputChannelAttributeName() {
        return "request-channel";
    }

    @Override
    protected BeanDefinitionBuilder parseHandler(Element element, ParserContext parserContext) {
        return new S3MessageHandlerParser(true)
            .parse(element, parserContext)
            .setProperty("reply-channel", "outputChannelName")
            .setProperty("reply-timeout", "sendTimeout")
            .getBeanDefinitionBuilder()
            ;
    }
}
