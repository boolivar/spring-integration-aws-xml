package org.springframework.integration.aws.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.core.convert.converter.Converter;
import org.springframework.integration.aws.config.xml.test.ParserTestBase;
import org.springframework.integration.aws.inbound.kinesis.CheckpointMode;
import org.springframework.integration.aws.inbound.kinesis.KinesisMessageDrivenChannelAdapter;
import org.springframework.integration.aws.inbound.kinesis.KinesisShardOffset;
import org.springframework.integration.aws.inbound.kinesis.ListenerMode;
import org.springframework.integration.mapping.InboundMessageMapper;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.support.locks.LockRegistry;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.Shard;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class KinesisMessageDrivenChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private KinesisAsyncClient kinesisAsyncClient;

    @Mock
    private ConcurrentMetadataStore checkpointStore;

    @Mock
    private Executor consumerExecutor;

    @Mock
    private Executor dispatcherExecutor;

    @Mock
    private LockRegistry lockRegistry;

    @Mock
    private Converter<byte[], Object> converter;

    @Mock
    private InboundMessageMapper<byte[]> headersMapper;

    @Mock
    private Function<List<Shard>, List<Shard>> shardListFilter;

    @Mock
    private KinesisShardOffset streamInitialSequence;

    @ConstructionMock(KinesisMessageDrivenChannelAdapter.class)
    @Test
    void testAdapter() {
        registerBean("kc", KinesisAsyncClient.class, kinesisAsyncClient);
        registerBean("cs", ConcurrentMetadataStore.class, checkpointStore);
        registerBean("cex", Executor.class, consumerExecutor);
        registerBean("dex", Executor.class, dispatcherExecutor);
        registerBean("lr", LockRegistry.class, lockRegistry);
        registerBean("cv", Converter.class, converter);
        registerBean("ehm", InboundMessageMapper.class, headersMapper);
        registerBean("slf", Function.class, shardListFilter);
        registerBean("sis", KinesisShardOffset.class, streamInitialSequence);

        parse("""
            <int-aws:kinesis-message-driven-channel-adapter kinesis-client="kc" streams="a,b,c"
                    id="i"
                    channel="c"
                    bind-source-record="#{true}"
                    checkpoint-mode="#{'batch'}"
                    checkpoint-store="cs"
                    checkpoints-interval="#{50}"
                    concurrency="#{5}"
                    consumer-backoff="#{50}"
                    consumer-executor="cex"
                    consumer-group="cg"
                    converter="cv"
                    dispatcher-executor="dex"
                    embedded-headers-mapper="ehm"
                    error-channel="ec"
                    idle-between-polls="#{50}"
                    listener-mode="#{'batch'}"
                    lock-registry="lr"
                    lock-renewal-timeout="#{50}"
                    records-limit="#{5}"
                    send-timeout="#{50}"
                    shard-list-filter="slf"
                    start-timeout="#{50}"
                    stream-initial-sequence="sis"/>
            """);

        var adapter = getBean(KinesisMessageDrivenChannelAdapter.class);

        verify(adapter).setBindSourceRecord(true);
        verify(adapter).setCheckpointMode(CheckpointMode.batch);
        verify(adapter).setCheckpointStore(checkpointStore);
        verify(adapter).setCheckpointsInterval(50);
        verify(adapter).setConcurrency(5);
        verify(adapter).setConsumerBackoff(50);
        verify(adapter).setConsumerExecutor(consumerExecutor);
        verify(adapter).setConsumerGroup("cg");
        verify(adapter).setConverter(converter);
        verify(adapter).setDispatcherExecutor(dispatcherExecutor);
        verify(adapter).setEmbeddedHeadersMapper(headersMapper);
        verify(adapter).setErrorChannelName("ec");
        verify(adapter).setIdleBetweenPolls(50);
        verify(adapter).setListenerMode(ListenerMode.batch);
        verify(adapter).setLockRegistry(lockRegistry);
        verify(adapter).setLockRenewalTimeout(50);
        verify(adapter).setRecordsLimit(5);
        verify(adapter).setSendTimeout(50);
        verify(adapter).setShardListFilter(shardListFilter);
        verify(adapter).setStartTimeout(50);
        verify(adapter).setStreamInitialSequence(streamInitialSequence);
    }

    void testAdapter(KinesisMessageDrivenChannelAdapter mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(kinesisAsyncClient, new String[] { "a", "b", "c" });
    }
}
