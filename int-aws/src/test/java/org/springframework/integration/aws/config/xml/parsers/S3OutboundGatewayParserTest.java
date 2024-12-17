package org.springframework.integration.aws.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.outbound.S3MessageHandler;
import org.springframework.integration.aws.outbound.S3MessageHandler.Command;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

class S3OutboundGatewayParserTest extends ParserTestBase {

    @Mock
    private S3AsyncClient s3Client;

    @Mock
    private BiConsumer<PutObjectRequest.Builder, Message<?>> uploadMetadataProvider;

    @ConstructionMock(S3MessageHandler.class)
    @Test
    void testMessageHandler() {
        registerBean("sss", S3AsyncClient.class, s3Client);
        registerBean("ump", BiConsumer.class, uploadMetadataProvider);
        registerBean("rq", DirectChannel.class, new DirectChannel());

        var handler = loadBean(S3MessageHandler.class, """
            <int-aws:s3-outbound-gateway
                    id="s3gw"
                    s3="sss"
                    async="#{true}"
                    bucket="#{'bkt'}"
                    command="#{'COPY'}"
                    key-expression="key.exp"
                    destination-key-expression="dest.key.exp"
                    destination-bucket-expression="dest.bkt.exp"
                    order="#{5}"
                    request-channel="rq"
                    reply-channel="rpl"
                    reply-timeout="#{50}"
                    upload-metadata-provider="ump"/>
            """);

        verify(handler).setAsync(true);
        verify(handler).setCommand(Command.COPY);
        verify(handler).setKeyExpression(argThat(e -> "key.exp".equals(e.getExpressionString())));
        verify(handler).setDestinationKeyExpression(argThat(e -> "dest.key.exp".equals(e.getExpressionString())));
        verify(handler).setDestinationBucketExpression(argThat(e -> "dest.bkt.exp".equals(e.getExpressionString())));
        verify(handler).setOrder(5);
        verify(handler).setSendTimeout(50L);
        verify(handler).setOutputChannelName("rpl");
        verify(handler).setUploadMetadataProvider(uploadMetadataProvider);
    }

    void testMessageHandler(S3MessageHandler mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(s3Client, "bkt", true);
    }
}
