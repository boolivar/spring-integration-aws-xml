package org.springframework.integration.aws.config.xml;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.aws.outbound.S3MessageHandler;
import org.w3c.dom.Element;

public class S3MessageHandlerParser {

    private final boolean produceReply;

    public S3MessageHandlerParser(boolean produceReply) {
        this.produceReply = produceReply;
    }

    public XmlBeanDefinitionBuilder parse(Element element, ParserContext parserContext) {
        return XmlBeanDefinitionBuilder.newInstance(element, parserContext, S3MessageHandler.class)
            .addExclusiveConstructorArgReference("s3", "transfer-manager")
            .addExclusiveConstructorArgValue("bucket", "bucket-expression", TypedStringValue::new, new ExpressionBeanDefinitionFactory()::createBeanDefinition)
            .configure(def -> def.addConstructorArgValue(produceReply))
            .setPropertyOrExpressionIfAttributeDefined("key")
            .setPropertyOrExpressionIfAttributeDefined("destination-bucket")
            .setPropertyOrExpressionIfAttributeDefined("destination-key")
            .setPropertyOrExpressionIfAttributeDefined("command")
            .setPropertyReferenceIfAttributeDefined("upload-metadata-provider")
            ;
    }
}
