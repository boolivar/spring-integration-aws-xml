package org.springframework.integration.aws.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;
import org.bool.junit.mockito.inline.StaticMock;

import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryDeserializer;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedConstruction.Context;
import org.mockito.MockedStatic;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.aws.config.xml.test.ParserTestBase;
import org.springframework.integration.aws.inbound.kinesis.CheckpointMode;
import org.springframework.integration.aws.inbound.kinesis.KclMessageDrivenChannelAdapter;
import org.springframework.integration.aws.inbound.kinesis.ListenerMode;
import org.springframework.integration.mapping.InboundMessageMapper;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.InitialPositionInStreamExtended;
import software.amazon.kinesis.metrics.MetricsLevel;

import static org.assertj.core.api.Assertions.assertThat;

@StaticMock({KinesisAsyncClient.class, CloudWatchAsyncClient.class, DynamoDbAsyncClient.class})
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
    void testDefaults(MockedConstruction<KclMessageDrivenChannelAdapter> mocked,
            MockedStatic<KinesisAsyncClient> kinesisClientMock,
            MockedStatic<CloudWatchAsyncClient> cloudWatchMock,
            MockedStatic<DynamoDbAsyncClient> dynamoDbMock) {

        kinesisClientMock.when(KinesisAsyncClient::create).thenReturn(kinesisClient);
        dynamoDbMock.when(DynamoDbAsyncClient::create).thenReturn(dynamoDbClient);
        cloudWatchMock.when(CloudWatchAsyncClient::create).thenReturn(cloudWatchClient);

        parse("""
                <int-aws:kcl-message-driven-channel-adapter streams="s"
                        channel="c"/>
            """);

        var adapter = getBean(KclMessageDrivenChannelAdapter.class);

        assertThat(mocked.constructed())
            .singleElement().isSameAs(adapter);
    }

    void testDefaults(KclMessageDrivenChannelAdapter mock, Context context) {
        verifyConstructorArgs(mock, context);
    }

    @Test
    void testKinesisClientConfig(MockedConstruction<KclMessageDrivenChannelAdapter> mocked,
            MockedStatic<CloudWatchAsyncClient> cloudWatchMock,
            MockedStatic<DynamoDbAsyncClient> dynamoDbMock) {
        registerBean("kc", KinesisAsyncClient.class, kinesisClient);

        dynamoDbMock.when(DynamoDbAsyncClient::create).thenReturn(dynamoDbClient);
        cloudWatchMock.when(CloudWatchAsyncClient::create).thenReturn(cloudWatchClient);

        parse("""
                <int-aws:kcl-message-driven-channel-adapter streams="s"
                        channel="c"
                        kinesis-client="kc"/>
            """);

        var adapter = getBean(KclMessageDrivenChannelAdapter.class);

        assertThat(mocked.constructed())
            .singleElement().isSameAs(adapter);
    }

    void testKinesisClientConfig(KclMessageDrivenChannelAdapter mock, Context context) {
        verifyConstructorArgs(mock, context);
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

        parse("""
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

        var adapter = getBean(KclMessageDrivenChannelAdapter.class);

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

    void testAdapter(KclMessageDrivenChannelAdapter mock, Context context) {
        verifyConstructorArgs(mock, context);
    }

    void verifyConstructorArgs(KclMessageDrivenChannelAdapter mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .containsExactly(kinesisClient, cloudWatchClient, dynamoDbClient, new String[] {"s"});
    }
}
