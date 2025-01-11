package org.springframework.integration.aws.support.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.config.xml.test.ParserTestBase;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.messaging.converter.MessageConverter;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

@ConstructionMock(SqsMessageHandler.class)
class SqsOutboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private SqsAsyncClient sqs;

    @Mock
    private MessageConverter messageConverter;

    @Test
    void testMessageHandler() {
        registerBean("sqs", sqs);
        registerBean("mc", messageConverter);

        parse("""
            <int-aws:sqs-outbound-channel-adapter
                    id="soca"
                    channel="c"
                    sqs="sqs"
                    sync="#{false}"
                    message-converter="mc"
                    error-message-strategy="ems"
                    success-channel="sc"
                    queue="q"
                    send-timeout="#{50}"
                    delay="#{5}"
                    message-deduplication-id-expression="mdi.exp"
                    message-group-id-expression="mgi.exp"
                    failure-channel="unknownChannel"
                    resource-id-resolver="unknownResolver"
                    async-handler="unknownHandler"
                    />
            """);

        var handler = beanFactory.getBean(SqsMessageHandler.class);

        verify(handler).setQueue("q");
        verify(handler).setAsync(true);
        verify(handler).setMessageConverter(messageConverter);
        verify(handler).setOutputChannelName("sc");
        verify(handler).setMessageDeduplicationIdExpressionString("mdi.exp");
        verify(handler).setMessageGroupIdExpressionString("mgi.exp");
        verify(handler).setDelay(5);
        verify(handler).setSendTimeout(50L);
    }

    void testMessageHandler(SqsMessageHandler mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(sqs);
    }
}
