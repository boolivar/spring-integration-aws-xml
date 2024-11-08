package org.springframework.integration.aws.config.xml;

import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.messaging.converter.MessageConverter;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

class SqsOutboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private SqsAsyncClient sqs;

    @Mock
    private MessageConverter messageConverter;

    @Test
    void testParser() {
        registerBean("sqs", SqsAsyncClient.class, sqs);
        registerBean("mc", MessageConverter.class, messageConverter);

        try (var mocked = mockConstruction(SqsMessageHandler.class, (mock, context) -> assertThat(context.arguments().get(0)).isSameAs(sqs))) {
            var handler = loadBean(SqsMessageHandler.class, """
                <int-aws:sqs-outbound-channel-adapter sqs="sqs"
                        channel="in"
                        async="#{true}"
                        delay="#{50}"
                        queue="#{'q'}"
                        order="#{5}"
                        output-channel="#{'out'}"
                        queue-not-found-strategy="#{'CREATE'}"
                        send-timeout="#{50}"
                        message-deduplication-id="#{'dd'}"
                        message-group-id="#{'mg'}"
                        message-converter="mc"/>
                """);

            assertThat(mocked.constructed()).size().isOne();

            verify(handler).setAsync(true);
            verify(handler).setDelay(50);
            verify(handler).setQueue("q");
            verify(handler).setOrder(5);
            verify(handler).setOutputChannelName("out");
            verify(handler).setQueueNotFoundStrategy(QueueNotFoundStrategy.CREATE);
            verify(handler).setSendTimeout(50);
            verify(handler).setMessageDeduplicationId("dd");
            verify(handler).setMessageGroupId("mg");
            verify(handler).setMessageConverter(messageConverter);
        }
    }
}
