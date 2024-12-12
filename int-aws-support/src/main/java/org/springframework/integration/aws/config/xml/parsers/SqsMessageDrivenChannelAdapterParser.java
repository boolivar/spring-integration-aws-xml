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
            .unsupportedAttributeWarning("resource-id-resolver")
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
        if ("NO_REDRIVE".equals(element.getAttribute("message-deletion-policy"))) {
            parserContext.getReaderContext()
                .warning("NO_REDRIVE message-deletion-policy is not supported and will be handled as ON_SUCCESS", element);
        }
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, SqsContainerOptionsFactoryBean.class)
            .setPropertyIfAttributeDefined("max-number-of-messages")
            .setPropertyIfAttributeDefined("visibility-timeout")
            .setPropertyIfAttributeDefined("queue-stop-timeout")
            .setPropertyIfAttributeDefined("wait-time-out")
            .setPropertyIfAttributeDefined("message-deletion-policy")
            .setPropertyIfAttributeDefined("fail-on-missing-queue")
            .setPropertyReferenceIfAttributeDefined("task-executor")
            .getBeanDefinitionBuilder()
            .applyCustomizers(bean -> bean.setAutowireCandidate(false))
            .getBeanDefinition();
    }
}
