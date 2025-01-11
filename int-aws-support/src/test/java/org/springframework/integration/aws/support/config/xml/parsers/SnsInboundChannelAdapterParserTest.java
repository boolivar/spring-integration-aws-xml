package org.springframework.integration.aws.support.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.config.xml.test.ParserTestBase;
import org.springframework.integration.aws.inbound.SnsInboundChannelAdapter;
import software.amazon.awssdk.services.sns.SnsClient;

import static org.assertj.core.api.Assertions.assertThat;

@ConstructionMock(SnsInboundChannelAdapter.class)
class SnsInboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private SnsClient sns;

    @Test
    void testAdapter() {
        registerBean("sns", sns);

        parse("""
            <int-aws:sns-inbound-channel-adapter
                    id="sica"
                    channel="c"
                    sns="sns"
                    path="/path/{uri},/path/*.do"
                    error-channel="ec"
                    handle-notification-status="#{true}"
                    payload-expression="payload.exp"
                    send-timeout="#{50}"
                    />
            """);

        var adapter = beanFactory.getBean(SnsInboundChannelAdapter.class);

        verify(adapter).setBeanName("sica");
        verify(adapter).setErrorChannelName("ec");
        verify(adapter).setHandleNotificationStatus(true);
        verify(adapter).setPayloadExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("payload.exp")));
        verify(adapter).setRequestTimeout(50L);
    }

    void testAdapter(SnsInboundChannelAdapter mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(sns, new String[] {"/path/{uri}", "/path/*.do"});
    }
}
