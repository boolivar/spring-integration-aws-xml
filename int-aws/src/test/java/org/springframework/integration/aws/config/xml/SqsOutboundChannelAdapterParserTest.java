package org.springframework.integration.aws.config.xml;

import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.integration.aws.outbound.SqsMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConverter;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

class SqsOutboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private SqsAsyncClient sqs;

    @Mock
    private MessageConverter messageConverter;

    @Mock
    private MessageChannel out;

    @Test
    void testParser() {
        registerBean("sqs", SqsAsyncClient.class, sqs);
        registerBean("mc", MessageConverter.class, messageConverter);
        when(channelResolver.resolveDestination("out"))
            .thenReturn(out);

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

        assertThat(handler)
            .returns(out, SqsMessageHandler::getOutputChannel)
            .returns(5, SqsMessageHandler::getOrder)
            .hasFieldOrPropertyWithValue("amazonSqs", sqs)
            .hasFieldOrPropertyWithValue("messageConverter", messageConverter)
            .hasFieldOrPropertyWithValue("async", true)
            .hasFieldOrPropertyWithValue("queueNotFoundStrategy", QueueNotFoundStrategy.CREATE)
            .hasFieldOrPropertyWithValue("queueExpression.value", "q")
            .hasFieldOrPropertyWithValue("delayExpression.value", 50)
            .hasFieldOrPropertyWithValue("sendTimeoutExpression.value", 50L)
            .hasFieldOrPropertyWithValue("messageGroupIdExpression.value", "mg")
            .hasFieldOrPropertyWithValue("messageDeduplicationIdExpression.value", "dd")
            ;
    }
}
