package org.springframework.integration.aws.support.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import io.awspring.cloud.sqs.listener.SqsContainerOptions;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.aws.inbound.SqsMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.core.DestinationResolver;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ConstructionMock(SqsMessageDrivenChannelAdapter.class)
class SqsMessageDrivenChannelAdapterParserTest extends ParserTestBase {

    @Captor
    private ArgumentCaptor<SqsContainerOptions> containerOptions;
    
    @Mock
    private SqsAsyncClient sqs;

    @Mock
    private TaskExecutor taskExecutor;

    @Mock
    private DestinationResolver<MessageChannel> destinationResolver;

    @Test
    void testAdapter() {
        registerBean("sqs", sqs);
        registerBean("ex", taskExecutor);
        registerBean("dr", destinationResolver);

        parse("""
            <int-aws:sqs-message-driven-channel-adapter
                    id="smdca"
                    channel="c"
                    queues="q1,q2"
                    sqs="sqs"
                    destination-resolver="dr"
                    error-channel="ec"
                    max-number-of-messages="#{5}"
                    message-deletion-policy="#{'ON_SUCCESS'}"
                    queue-stop-timeout="#{50}"
                    send-timeout="#{50}"
                    task-executor="ex"
                    visibility-timeout="#{50}"
                    wait-time-out="#{5}"
                    resource-id-resolver="unknownResolver"
                    fail-on-missing-queue="true"
                    />
            """);
        
        var adapter = beanFactory.getBean(SqsMessageDrivenChannelAdapter.class);

        verify(adapter).setBeanName("smdca");
        verify(adapter).setOutputChannelName("c");
        verify(adapter).setErrorChannelName("ec");
        verify(adapter).setSendTimeout(50);
        verify(adapter).setSqsContainerOptions(containerOptions.capture());

        assertThat(containerOptions.getValue())
            .returns(AcknowledgementMode.ON_SUCCESS, SqsContainerOptions::getAcknowledgementMode)
            .returns(5, SqsContainerOptions::getMaxConcurrentMessages)
            .returns(5, SqsContainerOptions::getMaxMessagesPerPoll)
            .returns(Duration.ofMillis(50), SqsContainerOptions::getListenerShutdownTimeout)
            .returns(Duration.ofSeconds(50), SqsContainerOptions::getMessageVisibility)
            .returns(Duration.ofSeconds(5), SqsContainerOptions::getPollTimeout)
            .returns(taskExecutor, SqsContainerOptions::getComponentsTaskExecutor)
            .returns(QueueNotFoundStrategy.FAIL, SqsContainerOptions::getQueueNotFoundStrategy)
            ;
    }

    void testBeanDefinition(SqsMessageDrivenChannelAdapter mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(sqs, new String[] {"q1", "q2"});
    }
}
