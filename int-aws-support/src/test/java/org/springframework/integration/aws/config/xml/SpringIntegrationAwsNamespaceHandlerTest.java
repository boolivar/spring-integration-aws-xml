package org.springframework.integration.aws.config.xml;

import org.assertj.core.api.Condition;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpringIntegrationAwsNamespaceHandlerTest {

    private final SpringIntegrationAwsNamespaceHandler handler = new SpringIntegrationAwsNamespaceHandler();

    @Test
    void test() {
        handler.init();
        assertThat(handler)
            .extracting("parsers", InstanceOfAssertFactories.MAP)
            .hasEntrySatisfying("sqs-outbound-channel-adapter", instanceOf(SqsOutboundChannelAdapterParser.class))
            .hasEntrySatisfying("sqs-message-driven-channel-adapter", instanceOf(SqsMessageDrivenChannelAdapterParser.class))
            .hasEntrySatisfying("s3-inbound-channel-adapter", instanceOf(S3InboundChannelAdapterParser.class))
            .hasEntrySatisfying("s3-inbound-streaming-channel-adapter", instanceOf(S3InboundStreamingChannelAdapterParser.class))
            .hasEntrySatisfying("s3-outbound-channel-adapter", instanceOf(S3OutboundChannelAdapterParser.class))
            .hasEntrySatisfying("s3-outbound-gateway", instanceOf(S3OutboundGatewayParser.class))
            .hasEntrySatisfying("sns-inbound-channel-adapter", instanceOf(SnsInboundChannelAdapterParser.class))
            ;
    }

    private Condition<Object> instanceOf(Class<?> type) {
        return new Condition<>(type::isInstance, "instance of " + type);
    }
}
