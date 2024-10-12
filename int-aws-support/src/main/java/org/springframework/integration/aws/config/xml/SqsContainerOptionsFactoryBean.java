package org.springframework.integration.aws.config.xml;

import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import io.awspring.cloud.sqs.listener.SqsContainerOptions;
import io.awspring.cloud.sqs.listener.SqsContainerOptionsBuilder;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.task.TaskExecutor;

import java.time.Duration;

class SqsContainerOptionsFactoryBean implements FactoryBean<SqsContainerOptions> {

    private final SqsContainerOptionsBuilder builder = SqsContainerOptions.builder();

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
        builder.messageVisibility(Duration.ofSeconds(seconds));
    }

    public void setMaxNumberOfMessages(int maxMessages) {
        builder.maxConcurrentMessages(maxMessages)
            .maxMessagesPerPoll(maxMessages);
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        builder.componentsTaskExecutor(taskExecutor);
    }

    public void setQueueStopTimeout(long millis) {
        builder.listenerShutdownTimeout(Duration.ofMillis(millis));
    }

    public void setWaitTimeOut(long seconds) {
        builder.pollTimeout(Duration.ofSeconds(seconds));
    }

    public void setMessageDeletionPolicy(String policy) {
        var mode = switch (policy) {
            case "NO_REDRIVE" -> AcknowledgementMode.ON_SUCCESS;
            case "NEVER" -> AcknowledgementMode.MANUAL;
            default -> AcknowledgementMode.valueOf(policy);
        };
        builder.acknowledgementMode(mode);
    }

    public void setFailOnMissingQueue(boolean fail) {
        builder.queueNotFoundStrategy(fail
            ? QueueNotFoundStrategy.FAIL
            : QueueNotFoundStrategy.CREATE);
    }
}
