package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.inbound.SqsMessageDrivenChannelAdapter;
import org.springframework.integration.config.xml.AbstractChannelAdapterParser;
import org.w3c.dom.Element;

public class SqsMessageDrivenChannelAdapterParser extends AbstractChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition doParse(Element element, ParserContext parserContext, String channelName) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, SqsMessageDrivenChannelAdapter.class)
            .unsupportedAttributeWarning("resource-id-resolver")
            .addConstructorArgReference("sqs")
            .addConstructorArgValue("queues")
            .setPropertyValueIfAttributeDefined("send-timeout")
            .setPropertyValueIfAttributeDefined("errorChannelName", "error-channel")
            .setPropertyReferenceIfAttributeDefined("channelResolver", "destination-resolver")
            .getBeanDefinitionBuilder()
            .addPropertyValue("outputChannelName", channelName)
            .addPropertyValue("sqsContainerOptions", sqsContainerOptions(element, parserContext))
            .getBeanDefinition();
    }

    private AbstractBeanDefinition sqsContainerOptions(Element element, ParserContext parserContext) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, SqsContainerOptionsFactoryBean.class)
            .setPropertyValueIfAttributeDefined("max-number-of-messages")
            .setPropertyValueIfAttributeDefined("visibility-timeout")
            .setPropertyValueIfAttributeDefined("queue-stop-timeout")
            .setPropertyValueIfAttributeDefined("wait-time-out")
            .setPropertyValueIfAttributeDefined("message-deletion-policy")
            .setPropertyValueIfAttributeDefined("fail-on-missing-queue")
            .setPropertyReferenceIfAttributeDefined("task-executor")
            .getBeanDefinitionBuilder()
            .applyCustomizers(bean -> bean.setAutowireCandidate(false))
            .getBeanDefinition();
    }
}
