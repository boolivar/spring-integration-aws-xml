package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.outbound.S3MessageHandler;
import org.springframework.integration.config.ExpressionFactoryBean;
import org.w3c.dom.Element;

public class S3MessageHandlerParser {

    private final boolean produceReply;

    public S3MessageHandlerParser(boolean produceReply) {
        this.produceReply = produceReply;
    }

    public XmlBeanDefinitionBuilder parse(Element element, ParserContext parserContext) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, S3MessageHandler.class)
            .addExclusiveConstructorArgReference("s3", "transfer-manager")
            .addExclusiveConstructorArgValue("bucket", "bucket-expression", TypedStringValue::new, this::expression)
            .configure(def -> def.addConstructorArgValue(produceReply))
            .setPropertyValueIfAttributeDefined("key-expression")
            .setPropertyValueIfAttributeDefined("destination-bucket-expression")
            .setPropertyValueIfAttributeDefined("destination-key-expression")
            .setPropertyValueIfExclusiveAttributeDefined("command", "command-expression")
            .setPropertyReferenceIfAttributeDefined("upload-metadata-provider")
            ;
    }

    private AbstractBeanDefinition expression(String value) {
        return BeanDefinitionBuilder.genericBeanDefinition(ExpressionFactoryBean.class)
            .addConstructorArgValue(value)
            .applyCustomizers(def -> def.setAutowireCandidate(false))
            .getBeanDefinition();
    }
}
