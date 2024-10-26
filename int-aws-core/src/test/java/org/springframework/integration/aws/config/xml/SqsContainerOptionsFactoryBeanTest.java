package org.springframework.integration.aws.config.xml;

import io.awspring.cloud.sqs.listener.QueueNotFoundStrategy;
import io.awspring.cloud.sqs.listener.SqsContainerOptions;
import io.awspring.cloud.sqs.listener.SqsContainerOptionsBuilder;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.FactoryBean;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SqsContainerOptionsFactoryBeanTest {

    @Mock(answer = Answers.RETURNS_SELF)
    private SqsContainerOptionsBuilder builder;

    @InjectMocks
    private SqsContainerOptionsFactoryBean factory;

    @AfterEach
    void verify() {
        verifyNoMoreInteractions(builder);
    }

    @Test
    void testFactory() {
        assertThat(factory)
            .returns(false, FactoryBean::isSingleton)
            .returns(SqsContainerOptions.class, FactoryBean::getObjectType)
            ;
    }

    @Test
    void testFactoryObject(@Mock SqsContainerOptions options) {
        given(builder.build())
            .willReturn(options);
        assertThat(factory.getObject())
            .isSameAs(options);
    }

    @Test
    void testVisibilityTimeout() {
        factory.setVisibilityTimeout(5);
        then(builder).should()
            .messageVisibility(Duration.ofSeconds(5));
    }

    @Test
    void testWaitTimeout() {
        factory.setWaitTimeOut(7);
        then(builder).should()
            .pollTimeout(Duration.ofSeconds(7));
    }

    @Test
    void testMaxNumberOfMessages() {
        factory.setMaxNumberOfMessages(9);
        then(builder).should()
            .maxConcurrentMessages(9);
        then(builder).should()
            .maxMessagesPerPoll(9);
    }

    @CsvSource({
        "NO_REDRIVE, ON_SUCCESS",
        "NEVER, MANUAL",
        "ON_SUCCESS, ON_SUCCESS",
        "ALWAYS, ALWAYS"
    })
    @ParameterizedTest
    void testMessageDeletionPolicy(String policy, AcknowledgementMode mode) {
        factory.setMessageDeletionPolicy(policy);
        then(builder).should()
            .acknowledgementMode(mode);
    }

    @CsvSource({
        "true, FAIL",
        "false, CREATE"
    })
    @ParameterizedTest
    void testFailOnMissingQueueProperty(boolean fail, QueueNotFoundStrategy strategy) {
        factory.setFailOnMissingQueue(fail);
        then(builder).should()
            .queueNotFoundStrategy(strategy);
    }
}
