package org.springframework.integration.aws.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import com.amazonaws.services.kinesis.producer.KinesisProducer;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.config.xml.test.ParserTestBase;
import org.springframework.integration.aws.outbound.KplMessageHandler;
import org.springframework.integration.mapping.OutboundMessageMapper;
import org.springframework.messaging.converter.MessageConverter;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class KplOutboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private KinesisProducer kinesisProducer;

    @Mock
    private OutboundMessageMapper<byte[]> messageMapper;

    @Mock
    private MessageConverter messageConverter;

    @ConstructionMock(KplMessageHandler.class)
    @Test
    void testMessageHandler() {
        registerBean("kp", KinesisProducer.class, kinesisProducer);
        registerBean("ehm", OutboundMessageMapper.class, messageMapper);
        registerBean("mc", MessageConverter.class, messageConverter);

        parse("""
            <int-aws:kpl-outbound-channel-adapter kinesis-producer="kp"
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
                    flush-duration="#{50}"
                    glue-schema-expression="payload.schema"
                    sequence-number-expression="headers.exp"/>
            """);

        var handler = getBean(KplMessageHandler.class);

        verify(handler).setStream("s");
        verify(handler).setAsync(true);
        verify(handler).setEmbeddedHeadersMapper(messageMapper);
        verify(handler).setExplicitHashKey("hk");
        verify(handler).setMessageConverter(messageConverter);
        verify(handler).setOrder(5);
        verify(handler).setOutputChannelName("out");
        verify(handler).setPartitionKey("pk");
        verify(handler).setSendTimeout(50);
        verify(handler).setFlushDuration(Duration.ofMillis(50));
        verify(handler).setGlueSchemaExpressionString("payload.schema");
        verify(handler).setSequenceNumberExpressionString("headers.exp");
    }

    void testMessageHandler(KplMessageHandler mock, Context context) {
        assertThat(context.arguments())
            .singleElement().isSameAs(kinesisProducer);
    }
}
