package org.springframework.integration.aws.support.config.xml.parsers;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.config.xml.parsers.S3MessageHandlerParser;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

public class S3OutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        return new S3MessageHandlerParser(false)
            .parse(element, parserContext)
            .unsupportedAttributeWarning("resource-id-resolver", "object-acl-expression", "progress-listener")
            .build();
    }
}