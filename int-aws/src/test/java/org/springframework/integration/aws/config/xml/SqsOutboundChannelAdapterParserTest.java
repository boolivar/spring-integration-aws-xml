package org.springframework.integration.aws.config.xml;

import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.integration.junit.ConstructionMock;
import org.springframework.messaging.converter.MessageConverter;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

class SqsOutboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private SqsAsyncClient sqs;

    @Mock
    private MessageConverter messageConverter;

    @ConstructionMock(SqsMessageHandler.class)
    @Test
    void testParser(MockedConstruction<SqsMessageHandler> mocked) {
        registerBean("sqs", SqsAsyncClient.class, sqs);
        registerBean("mc", MessageConverter.class, messageConverter);

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

        assertThat(mocked.constructed()).size().isOne();
    }

    void testParser(SqsMessageHandler mock, Context context) {
        assertThat(context.arguments())
            .singleElement().isSameAs(sqs);
    }
}
