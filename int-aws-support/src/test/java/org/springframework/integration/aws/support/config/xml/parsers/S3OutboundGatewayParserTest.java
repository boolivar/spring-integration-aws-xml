package org.springframework.integration.aws.support.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.config.xml.parsers.test.ParserTestBase;
import org.springframework.integration.aws.outbound.S3MessageHandler;
import org.springframework.integration.aws.outbound.S3MessageHandler.Command;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

@ConstructionMock(S3MessageHandler.class)
class S3OutboundGatewayParserTest extends ParserTestBase {

    @Mock
    private S3TransferManager transferManager;

    @Mock
    private BiConsumer<PutObjectRequest.Builder, Message<?>> uploadMetadataProvider;

    @Test
    void testMessageHandler() {
        registerBean("tm", transferManager);
        registerBean("ump", BiConsumer.class, uploadMetadataProvider);
        registerBean("rqc", new DirectChannel());

        parse("""
            <int-aws:s3-outbound-gateway
                    id="s3og"
                    transfer-manager="tm"
                    request-channel="rqc"
                    reply-channel="rplc"
                    reply-timeout="#{50}"
                    order="#{5}"
                    bucket="bkt"
                    command="#{'COPY'}"
                    upload-metadata-provider="ump"
                    key-expression="key.exp"
                    destination-key-expression="dest.key.exp"
                    destination-bucket-expression="dest.bkt.exp"
                    object-acl-expression="acl.exp"
                    progress-listener="unknownListener"
                    resource-id-resolver="unknownResolver"
                    />
            """);

        var handler = beanFactory.getBean(S3MessageHandler.class);

        verify(handler).setOutputChannelName("rplc");
        verify(handler).setSendTimeout(50L);
        verify(handler).setCommand(Command.COPY);
        verify(handler).setOrder(5);
        verify(handler).setUploadMetadataProvider(uploadMetadataProvider);
        verify(handler).setKeyExpression(argThat(e -> "key.exp".equals(e.getExpressionString())));
        verify(handler).setDestinationKeyExpression(argThat(e -> "dest.key.exp".equals(e.getExpressionString())));
        verify(handler).setDestinationBucketExpression(argThat(e -> "dest.bkt.exp".equals(e.getExpressionString())));

    }

    void testMessageHandler(S3MessageHandler mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(transferManager, "bkt", true);
    }
}
