package org.springframework.integration.aws.config.xml;

import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryDeserializer;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedConstruction.Context;
import org.mockito.MockedStatic;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.aws.inbound.kinesis.CheckpointMode;
import org.springframework.integration.aws.inbound.kinesis.KclMessageDrivenChannelAdapter;
import org.springframework.integration.aws.inbound.kinesis.ListenerMode;
import org.springframework.integration.junit.ConstructionMock;
import org.springframework.integration.junit.StaticMock;
import org.springframework.integration.mapping.InboundMessageMapper;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.InitialPositionInStreamExtended;
import software.amazon.kinesis.metrics.MetricsLevel;

import static org.assertj.core.api.Assertions.assertThat;

@ConstructionMock(KclMessageDrivenChannelAdapter.class)
class KclMessageDrivenChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private KinesisAsyncClient kinesisClient;

    @Mock
    private CloudWatchAsyncClient cloudWatchClient;

    @Mock
    private DynamoDbAsyncClient dynamoDbClient;

    @Mock
    private Converter<byte[], Object> converter;

    @Mock
    private InboundMessageMapper<byte[]> embeddedHeadersMapper;

    @Mock
    private TaskExecutor executor;

    @Mock
    private GlueSchemaRegistryDeserializer glueSchemaRegistryDeserializer;

    @Mock
    private InitialPositionInStreamExtended streamInitialSequence;

    @Test
    void testDefaults(MockedConstruction<KclMessageDrivenChannelAdapter> mocked) {
        var adapter = loadBean(KclMessageDrivenChannelAdapter.class, """
                <int-aws:kcl-message-driven-channel-adapter streams="s"
                        channel="c"/>
            """);
        assertThat(mocked.constructed())
            .singleElement().isSameAs(adapter);
    }

    void testDefaults(KclMessageDrivenChannelAdapter mock, Context context) {
        assertThat(context.arguments()).singleElement()
            .isEqualTo(new String[] {"s"});
    }

    @StaticMock({CloudWatchAsyncClient.class, DynamoDbAsyncClient.class})
    @Test
    void testKinesisClientConfig(MockedConstruction<KclMessageDrivenChannelAdapter> mocked,
            MockedStatic<CloudWatchAsyncClient> cloudWatchMock, MockedStatic<DynamoDbAsyncClient> dynamoDbMock) {
        registerBean("kc", KinesisAsyncClient.class, kinesisClient);

        dynamoDbMock.when(DynamoDbAsyncClient::create).thenReturn(dynamoDbClient);
        cloudWatchMock.when(CloudWatchAsyncClient::create).thenReturn(cloudWatchClient);

        var adapter = loadBean(KclMessageDrivenChannelAdapter.class, """
                <int-aws:kcl-message-driven-channel-adapter streams="s"
                        channel="c"
                        kinesis-client="kc"/>
            """);

        assertThat(mocked.constructed())
            .singleElement().isSameAs(adapter);
    }

    void testKinesisClientConfig(KclMessageDrivenChannelAdapter mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(kinesisClient, cloudWatchClient, dynamoDbClient);
    }

    @Test
    void testAdapter() {
        registerBean("kc", KinesisAsyncClient.class, kinesisClient);
        registerBean("cw", CloudWatchAsyncClient.class, cloudWatchClient);
        registerBean("dd", DynamoDbAsyncClient.class, dynamoDbClient);
        registerBean("cv", Converter.class, converter);
        registerBean("ehm", InboundMessageMapper.class, embeddedHeadersMapper);
        registerBean("ex", TaskExecutor.class, executor);
        registerBean("gsrd", GlueSchemaRegistryDeserializer.class, glueSchemaRegistryDeserializer);
        registerBean("sis", InitialPositionInStreamExtended.class, streamInitialSequence);

        var adapter = loadBean(KclMessageDrivenChannelAdapter.class, """
            <int-aws:kcl-message-driven-channel-adapter streams="s"
                    id="i"
                    channel="c"
                    kinesis-client="kc"
                    cloud-watch-client="cw"
                    dynamo-db-client="dd"
                    bind-source-record="#{true}"
                    checkpoint-mode="batch"
                    checkpoints-interval="#{70}"
                    consumer-backoff="#{70}"
                    consumer-group="cg"
                    converter="cv"
                    embedded-headers-mapper="ehm"
                    empty-record-list="#{true}"
                    error-channel="ec"
                    executor="ex"
                    fan-out="#{true}"
                    glue-schema-registry-deserializer="gsrd"
                    graceful-shutdown-timeout="#{70}"
                    lease-table-name="ltn"
                    listener-mode="#{'batch'}"
                    metrics-level="#{'SUMMARY'}"
                    polling-idle-time="#{70}"
                    polling-max-records="#{7}"
                    send-timeout="#{70}"
                    stream-initial-sequence="sis"
                    worker-id="wid"/>
            """);

        verify(adapter).setBindSourceRecord(true);
        verify(adapter).setCheckpointMode(CheckpointMode.batch);
        verify(adapter).setCheckpointsInterval(70L);
        verify(adapter).setConsumerBackoff(70);
        verify(adapter).setConsumerGroup("cg");
        verify(adapter).setConverter(converter);
        verify(adapter).setEmbeddedHeadersMapper(embeddedHeadersMapper);
        verify(adapter).setEmptyRecordList(true);
        verify(adapter).setErrorChannelName("ec");
        verify(adapter).setExecutor(executor);
        verify(adapter).setFanOut(true);
        verify(adapter).setGlueSchemaRegistryDeserializer(glueSchemaRegistryDeserializer);
        verify(adapter).setGracefulShutdownTimeout(70L);
        verify(adapter).setLeaseTableName("ltn");
        verify(adapter).setListenerMode(ListenerMode.batch);
        verify(adapter).setMetricsLevel(MetricsLevel.SUMMARY);
        verify(adapter).setPollingIdleTime(70L);
        verify(adapter).setStreamInitialSequence(streamInitialSequence);
        verify(adapter).setWorkerId("wid");
    }

    void testParser(KclMessageDrivenChannelAdapter mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(new String[] {"s"}, kinesisClient, cloudWatchClient, dynamoDbClient);
    }
}
