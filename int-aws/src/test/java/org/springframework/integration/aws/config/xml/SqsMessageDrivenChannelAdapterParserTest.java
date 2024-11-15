package org.springframework.integration.aws.config.xml;

import io.awspring.cloud.sqs.listener.BackPressureMode;
import io.awspring.cloud.sqs.listener.FifoBatchGroupingStrategy;
import io.awspring.cloud.sqs.listener.ListenerMode;
import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import io.awspring.cloud.sqs.listener.SqsContainerOptions;
import io.awspring.cloud.sqs.listener.acknowledgement.AcknowledgementOrdering;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import io.awspring.cloud.sqs.support.converter.MessagingMessageConverter;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.aws.inbound.SqsMessageDrivenChannelAdapter;
import org.springframework.integration.junit.ConstructionMock;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class SqsMessageDrivenChannelAdapterParserTest extends ParserTestBase {

    @Captor
    private ArgumentCaptor<SqsContainerOptions> containerOptions;
    
    @Mock
    private SqsAsyncClient sqs;

    @Mock
    private MessagingMessageConverter<?> messageConverter;

    @Mock
    private TaskExecutor taskExecutor;

    @ConstructionMock(SqsMessageDrivenChannelAdapter.class)
    @Test
    void testBeanDefinition() {
        registerBean("sqs", SqsAsyncClient.class, sqs);
        registerBean("mc", MessagingMessageConverter.class, messageConverter);
        registerBean("ex", TaskExecutor.class, taskExecutor);

        var adapter = loadBean(SqsMessageDrivenChannelAdapter.class, """
            <int-aws:sqs-message-driven-channel-adapter queues="q" sqs="sqs"
                    id="i"
                    channel="c"
                    error-channel="ec"
                    send-timeout="#{50}"
                    acknowledgement-interval="#{50}"
                    acknowledgement-ordering="#{'ORDERED_BY_GROUP'}"
                    acknowledgement-mode="#{'ALWAYS'}"
                    acknowledgement-threshold="#{5}"
                    back-pressure-mode="#{'FIXED_HIGH_THROUGHPUT'}"
                    queue-not-found-strategy="#{'FAIL'}"
                    fifo-batch-grouping-strategy="#{'PROCESS_MESSAGE_GROUPS_IN_PARALLEL_BATCHES'}"
                    listener-mode="#{'BATCH'}"
                    message-visibility="#{5}"
                    max-concurrent-messages="#{5}"
                    max-messages-per-poll="#{5}"
                    max-delay-between-polls="#{5}"
                    poll-timeout="#{5}"
                    listener-shutdown-timeout="#{5}"
                    message-converter="mc"
                    components-task-executor="ex"/>
            """);

        verify(adapter).setBeanName("i");
        verify(adapter).setOutputChannelName("c");
        verify(adapter).setErrorChannelName("ec");
        verify(adapter).setSendTimeout(50);
        verify(adapter).setSqsContainerOptions(containerOptions.capture());

        assertThat(containerOptions.getValue())
            .returns(Duration.ofMillis(50), SqsContainerOptions::getAcknowledgementInterval)
            .returns(AcknowledgementOrdering.ORDERED_BY_GROUP, SqsContainerOptions::getAcknowledgementOrdering)
            .returns(AcknowledgementMode.ALWAYS, SqsContainerOptions::getAcknowledgementMode)
            .returns(5, SqsContainerOptions::getAcknowledgementThreshold)
            .returns(BackPressureMode.FIXED_HIGH_THROUGHPUT, SqsContainerOptions::getBackPressureMode)
            .returns(QueueNotFoundStrategy.FAIL, SqsContainerOptions::getQueueNotFoundStrategy)
            .returns(FifoBatchGroupingStrategy.PROCESS_MESSAGE_GROUPS_IN_PARALLEL_BATCHES, SqsContainerOptions::getFifoBatchGroupingStrategy)
            .returns(ListenerMode.BATCH, SqsContainerOptions::getListenerMode)
            .returns(Duration.ofSeconds(5), SqsContainerOptions::getMessageVisibility)
            .returns(5, SqsContainerOptions::getMaxConcurrentMessages)
            .returns(5, SqsContainerOptions::getMaxMessagesPerPoll)
            .returns(Duration.ofSeconds(5), SqsContainerOptions::getMaxDelayBetweenPolls)
            .returns(Duration.ofSeconds(5), SqsContainerOptions::getPollTimeout)
            .returns(Duration.ofSeconds(5), SqsContainerOptions::getListenerShutdownTimeout)
            .returns(messageConverter, SqsContainerOptions::getMessageConverter)
            .returns(taskExecutor, SqsContainerOptions::getComponentsTaskExecutor)
            ;
    }

    void testBeanDefinition(SqsMessageDrivenChannelAdapter mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(sqs, new String[] {"q"});
    }
}
