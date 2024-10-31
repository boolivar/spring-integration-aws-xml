package org.springframework.integration.aws.config.xml;

import io.awspring.cloud.sqs.listener.BackPressureMode;
import io.awspring.cloud.sqs.listener.FifoBatchGroupingStrategy;
import io.awspring.cloud.sqs.listener.ListenerMode;
import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import io.awspring.cloud.sqs.listener.SqsContainerOptions;
import io.awspring.cloud.sqs.listener.SqsContainerOptionsBuilder;
import io.awspring.cloud.sqs.listener.acknowledgement.AcknowledgementOrdering;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import io.awspring.cloud.sqs.support.converter.MessagingMessageConverter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.task.TaskExecutor;

import java.time.Duration;

public class SqsContainerOptionsFactoryBean implements FactoryBean<SqsContainerOptions> {

    private final SqsContainerOptionsBuilder builder;

    public SqsContainerOptionsFactoryBean() {
        this(SqsContainerOptions.builder());
    }

    public SqsContainerOptionsFactoryBean(SqsContainerOptionsBuilder builder) {
        this.builder = builder;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public Class<SqsContainerOptions> getObjectType() {
        return SqsContainerOptions.class;
    }

    @Override
    public SqsContainerOptions getObject() {
        return builder.build();
    }

    public void setVisibilityTimeout(long seconds) {
        setMessageVisibility(seconds);
    }

    public void setMessageVisibility(long seconds) {
        builder.messageVisibility(Duration.ofSeconds(seconds));
    }

    public void setMaxNumberOfMessages(int maxMessages) {
        setMaxConcurrentMessages(maxMessages);
        setMaxMessagesPerPoll(maxMessages);
    }

    public void setMaxConcurrentMessages(int max) {
        builder.maxConcurrentMessages(max);
    }

    public void setMaxMessagesPerPoll(int max) {
        builder.maxMessagesPerPoll(max);
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        setComponentsTaskExecutor(taskExecutor);
    }

    public void setComponentsTaskExecutor(TaskExecutor taskExecutor) {
        builder.componentsTaskExecutor(taskExecutor);
    }

    public void setQueueStopTimeout(long millis) {
        setListenerShutdownTimeout(millis);
    }

    public void setListenerShutdownTimeout(long millis) {
        builder.listenerShutdownTimeout(Duration.ofMillis(millis));
    }

    public void setWaitTimeOut(long seconds) {
        setPollTimeout(seconds);
    }

    public void setPollTimeout(long seconds) {
        builder.pollTimeout(Duration.ofSeconds(seconds));
    }

    public void setMessageDeletionPolicy(String policy) {
        var mode = switch (policy) {
            case "NO_REDRIVE" -> AcknowledgementMode.ON_SUCCESS;
            case "NEVER" -> AcknowledgementMode.MANUAL;
            default -> AcknowledgementMode.valueOf(policy);
        };
        setAcknowledgementMode(mode);
    }

    public void setAcknowledgementMode(AcknowledgementMode acknowledgementMode) {
        builder.acknowledgementMode(acknowledgementMode);
    }

    public void setAcknowledgementOrdering(AcknowledgementOrdering acknowledgementOrdering) {
        builder.acknowledgementOrdering(acknowledgementOrdering);
    }

    public void setAcknowledgementInterval(int millis) {
        builder.acknowledgementInterval(Duration.ofMillis(millis));
    }

    public void setAcknowledgementThreshold(int threshold) {
        builder.acknowledgementThreshold(threshold);
    }

    public void setFailOnMissingQueue(boolean fail) {
        setQueueNotFoundStrategy(fail ? QueueNotFoundStrategy.FAIL : QueueNotFoundStrategy.CREATE);
    }

    public void setQueueNotFoundStrategy(QueueNotFoundStrategy strategy) {
        builder.queueNotFoundStrategy(strategy);
    }

    public void setMaxDelayBetweenPolls(int seconds) {
        builder.maxDelayBetweenPolls(Duration.ofSeconds(seconds));
    }

    public void setBackPressureMode(BackPressureMode backPressureMode) {
        builder.backPressureMode(backPressureMode);
    }

    public void setListenerMode(ListenerMode listenerMode) {
        builder.listenerMode(listenerMode);
    }
    
    public void setFifoBatchGroupingStrategy(FifoBatchGroupingStrategy strategy) {
        builder.fifoBatchGroupingStrategy(strategy);
    }

    public void setMessageConverter(MessagingMessageConverter<?> messageConverter) {
        builder.messageConverter(messageConverter);
    }
}
