package org.springframework.integration.aws.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.outbound.SnsMessageHandler;
import software.amazon.awssdk.services.sns.SnsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

class SnsOutboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private SnsAsyncClient sns;

    @ConstructionMock(SnsMessageHandler.class)
    @Test
    void testHandler() {
        registerBean("sns", SnsAsyncClient.class, sns);

        var handler = loadBean(SnsMessageHandler.class, """
            <int-aws:sns-outbound-channel-adapter
                    id="snsoca"
                    channel="c"
                    sns="sns"
                    topic-arn="arn"
                    subject="subj"
                    message-group-id="groupid"
                    body-expression="body.exp"
                    message-deduplication-id-expression="mdi.exp"
                    order="#{5}"
                    async="#{true}"
                    output-channel="out"
                    send-timeout="#{50}"/>
            """);

        verify(handler).setTopicArn("arn");
        verify(handler).setSubject("subj");
        verify(handler).setMessageGroupId("groupid");
        verify(handler).setBodyExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("body.exp")));
        verify(handler).setMessageDeduplicationIdExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("mdi.exp")));
        verify(handler).setOrder(5);
        verify(handler).setOutputChannelName("out");
        verify(handler).setSendTimeout(50L);
        verify(handler).setAsync(true);
    }

    void testHandler(SnsMessageHandler mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(sns);
    }
}
