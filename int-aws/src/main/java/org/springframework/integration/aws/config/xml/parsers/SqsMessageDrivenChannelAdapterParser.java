package org.springframework.integration.aws.config.xml.parsers;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.config.xml.SqsContainerOptionsFactoryBean;
import org.springframework.integration.aws.config.xml.XmlBeanDefinitionBuilder;
import org.springframework.integration.aws.inbound.SqsMessageDrivenChannelAdapter;
import org.springframework.integration.config.xml.AbstractChannelAdapterParser;
import org.w3c.dom.Element;

public class SqsMessageDrivenChannelAdapterParser extends AbstractChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition doParse(Element element, ParserContext parserContext, String channelName) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, SqsMessageDrivenChannelAdapter.class)
            .addConstructorArgReference("sqs")
            .addConstructorArgValue("queues")
            .setPropertyIfAttributeDefined("send-timeout")
            .setPropertyIfAttributeDefined("error-channel", "errorChannelName")
            .setPropertyReferenceIfAttributeDefined("destination-resolver", "channelResolver")
            .getBeanDefinitionBuilder()
            .addPropertyValue("outputChannelName", channelName)
            .addPropertyValue("sqsContainerOptions", sqsContainerOptions(element, parserContext))
            .getBeanDefinition();
    }

    private AbstractBeanDefinition sqsContainerOptions(Element element, ParserContext parserContext) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, SqsContainerOptionsFactoryBean.class)
            .setPropertyReferenceIfAttributeDefined("components-task-executor")
            .setPropertyReferenceIfAttributeDefined("message-converter")
            .setPropertyIfAttributeDefined("acknowledgement-interval")
            .setPropertyIfAttributeDefined("acknowledgement-mode")
            .setPropertyIfAttributeDefined("acknowledgement-ordering")
            .setPropertyIfAttributeDefined("acknowledgement-threshold")
            .setPropertyIfAttributeDefined("back-pressure-mode")
            .setPropertyIfAttributeDefined("fifo-batch-grouping-strategy")
            .setPropertyIfAttributeDefined("listener-mode")
            .setPropertyIfAttributeDefined("listener-shutdown-timeout")
            .setPropertyIfAttributeDefined("max-concurrent-messages")
            .setPropertyIfAttributeDefined("max-delay-between-polls")
            .setPropertyIfAttributeDefined("max-messages-per-poll")
            .setPropertyIfAttributeDefined("message-visibility")
            .setPropertyIfAttributeDefined("poll-timeout")
            .setPropertyIfAttributeDefined("queue-not-found-strategy")
            .getBeanDefinitionBuilder()
            .applyCustomizers(bean -> bean.setAutowireCandidate(false))
            .getBeanDefinition();
    }
}
