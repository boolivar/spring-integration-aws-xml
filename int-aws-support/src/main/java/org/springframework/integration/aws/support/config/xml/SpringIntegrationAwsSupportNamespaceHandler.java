package org.springframework.integration.aws.support.config.xml;

import org.springframework.integration.aws.config.xml.SpringIntegrationAwsNamespaceHandler;

public class SpringIntegrationAwsSupportNamespaceHandler extends SpringIntegrationAwsNamespaceHandler {

    SpringIntegrationAwsSupportNamespaceHandler() {
        super("META-INF/spring-integration-aws-support-element-parser.mapping");
    }
}
