package org.springframework.integration.aws.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.config.xml.parsers.test.ParserTestBase;
import org.springframework.integration.aws.outbound.KinesisMessageHandler;
import org.springframework.integration.mapping.OutboundMessageMapper;
import org.springframework.messaging.converter.MessageConverter;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

class KinesisOutboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private KinesisAsyncClient kinesisClient;

    @Mock
    private OutboundMessageMapper<byte[]> messageMapper;

    @Mock
    private MessageConverter messageConverter;

    @ConstructionMock(KinesisMessageHandler.class)
    @Test
    void testMessageHandler() {
        registerBean("kc", KinesisAsyncClient.class, kinesisClient);
        registerBean("ehm", OutboundMessageMapper.class, messageMapper);
        registerBean("mc", MessageConverter.class, messageConverter);

        parse("""
            <int-aws:kinesis-outbound-channel-adapter kinesis-client="kc"
                    id="i"
                    channel="c"
                    stream="s"
                    async="#{true}"
                    embedded-headers-mapper="ehm"
                    explicit-hash-key="hk"
                    message-converter="mc"
                    order="#{5}"
                    output-channel="out"
                    partition-key="pk"
                    send-timeout="#{50}"
                    sequence-number-expression="headers.exp"/>
            """);

        var handler = getBean(KinesisMessageHandler.class);

        verify(handler).setStream("s");
        verify(handler).setAsync(true);
        verify(handler).setEmbeddedHeadersMapper(messageMapper);
        verify(handler).setExplicitHashKey("hk");
        verify(handler).setMessageConverter(messageConverter);
        verify(handler).setOrder(5);
        verify(handler).setOutputChannelName("out");
        verify(handler).setPartitionKey("pk");
        verify(handler).setSendTimeout(50);
        verify(handler).setSequenceNumberExpressionString("headers.exp");
    }

    void testMessageHandler(KinesisMessageHandler mock, Context context) {
        assertThat(context.arguments())
            .singleElement().isSameAs(kinesisClient);
    }
}
