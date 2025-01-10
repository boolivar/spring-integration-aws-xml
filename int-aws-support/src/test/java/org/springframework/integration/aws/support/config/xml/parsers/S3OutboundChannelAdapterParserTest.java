package org.springframework.integration.aws.support.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.config.xml.parsers.test.ParserTestBase;
import org.springframework.integration.aws.outbound.S3MessageHandler;
import org.springframework.integration.aws.outbound.S3MessageHandler.Command;
import org.springframework.messaging.Message;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

class S3OutboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private S3TransferManager transferManager;

    @Mock
    private BiConsumer<PutObjectRequest.Builder, Message<?>> uploadMetadataProvider;

    @ConstructionMock(S3MessageHandler.class)
    @Test
    void testMessageHandler() {
        registerBean("tm", transferManager);
        registerBean("ump", uploadMetadataProvider);

        parse("""
            <int-aws:s3-outbound-channel-adapter
                    id="s3oca"
                    channel="c"
                    order="#{5}"
                    transfer-manager="tm"
                    bucket="bkt"
                    command="#{'COPY'}"
                    destination-bucket-expression="dest.bkt.exp"
                    destination-key-expression="dest.key.exp"
                    key-expression="key.exp"
                    object-acl-expression="acl.exp"
                    upload-metadata-provider="ump"
                    progress-listener="unknownListener"
                    resource-id-resolver="unknownResolver"
                    />
            """);

        var handler = beanFactory.getBean(S3MessageHandler.class);

        verify(handler).setOrder(5);
        verify(handler).setCommand(Command.COPY);
        verify(handler).setKeyExpression(argThat(e -> "key.exp".equals(e.getExpressionString())));
        verify(handler).setDestinationKeyExpression(argThat(e -> "dest.key.exp".equals(e.getExpressionString())));
        verify(handler).setDestinationBucketExpression(argThat(e -> "dest.bkt.exp".equals(e.getExpressionString())));
        verify(handler).setUploadMetadataProvider(uploadMetadataProvider);
    }

    void testMessageHandler(S3MessageHandler mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(transferManager, "bkt", false);
    }
}
