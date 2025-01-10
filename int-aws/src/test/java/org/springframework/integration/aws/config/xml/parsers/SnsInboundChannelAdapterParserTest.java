package org.springframework.integration.aws.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.config.xml.parsers.test.ParserTestBase;
import org.springframework.integration.aws.inbound.SnsInboundChannelAdapter;
import software.amazon.awssdk.services.sns.SnsClient;

import static org.assertj.core.api.Assertions.assertThat;

class SnsInboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private SnsClient sns;

    @ConstructionMock(SnsInboundChannelAdapter.class)
    @Test
    void testAdapter() {
        registerBean("sns", SnsClient.class, sns);

        parse("""
            <int-aws:sns-inbound-channel-adapter
                    auto-startup="#{false}"
                    id="snsica"
                    channel="c"
                    sns="sns"
                    path="/sns/{id},/sns/action/*.do"
                    error-channel="ec"
                    payload-expression="pe.exp"
                    handle-notification-status="#{true}"
                    send-timeout="#{50}"/>
            """);

        var adapter = getBean(SnsInboundChannelAdapter.class);

        verify(adapter).setBeanName("snsica");
        verify(adapter).setAutoStartup(false);
        verify(adapter).setRequestChannelName("c");
        verify(adapter).setErrorChannelName("ec");
        verify(adapter).setPayloadExpression(argThat(e -> "pe.exp".equals(e.getExpressionString())));
        verify(adapter).setHandleNotificationStatus(true);
        verify(adapter).setRequestTimeout(50L);
    }

    void testAdapter(SnsInboundChannelAdapter mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(sns, new String[] {"/sns/{id}", "/sns/action/*.do"});
    }
}
