package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.outbound.S3MessageHandler;
import org.springframework.integration.config.ExpressionFactoryBean;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.w3c.dom.Element;

public class S3OutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, S3MessageHandler.class)
            .addExclusiveConstructorArgReference("s3", "transfer-manager")
            .addExclusiveConstructorArgValue("bucket", "bucket-expression", TypedStringValue::new, this::expression)
            .setPropertyValueIfAttributeDefined("key-expression")
            .setPropertyValueIfAttributeDefined("destination-bucket-expression")
            .setPropertyValueIfAttributeDefined("destination-key-expression")
            .setPropertyValueIfExclusiveAttributeDefined("command", "command-expression")
            .setPropertyReferenceIfAttributeDefined("upload-metadata-provider")
            .build();
    }

    private AbstractBeanDefinition expression(String value) {
        return BeanDefinitionBuilder.genericBeanDefinition(ExpressionFactoryBean.class)
            .addConstructorArgValue(value)
            .applyCustomizers(def -> def.setAutowireCandidate(false))
            .getBeanDefinition();
    }
}
