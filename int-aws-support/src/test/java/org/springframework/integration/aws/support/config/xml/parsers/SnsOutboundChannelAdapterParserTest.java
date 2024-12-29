package org.springframework.integration.aws.support.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.outbound.SnsMessageHandler;
import software.amazon.awssdk.services.sns.SnsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

@ConstructionMock(SnsMessageHandler.class)
class SnsOutboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private SnsAsyncClient sns;

    @Test
    void testHandler() {
        registerBean("sns", sns);

        parse("""
            <int-aws:sns-outbound-channel-adapter
                    id="snsoca"
                    channel="c"
                    sns="sns"
                    topic-arn-expression="topic.exp"
                    subject-expression="subj.exp"
                    body-expression="body.exp"
                    message-group-id-expression="mgi.exp"
                    message-deduplication-id-expression="mdi.exp"
                    order="#{5}"
                    send-timeout="#{50}"
                    success-channel="sc"
                    sync="#{false}"
                    failure-channel="unknownChannel"
                    error-message-strategy="unknownStrategy"
                    resource-id-resolver="unknownResolver"
                    async-handler="unknownHandler"
                    />
            """);

        var handler = beanFactory.getBean(SnsMessageHandler.class);

        verify(handler).setTopicArnExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("topic.exp")));
        verify(handler).setSubjectExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("subj.exp")));
        verify(handler).setBodyExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("body.exp")));
        verify(handler).setMessageGroupIdExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("mgi.exp")));
        verify(handler).setMessageDeduplicationIdExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("mdi.exp")));
        verify(handler).setOrder(5);
        verify(handler).setOutputChannelName("sc");
        verify(handler).setSendTimeout(50L);
        verify(handler).setAsync(true);
    }

    void testHandler(SnsMessageHandler mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(sns);
    }
}
