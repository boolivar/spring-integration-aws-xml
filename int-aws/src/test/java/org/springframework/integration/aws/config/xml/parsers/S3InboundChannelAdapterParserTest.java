package org.springframework.integration.aws.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.inbound.S3InboundFileSynchronizer;
import org.springframework.integration.aws.inbound.S3InboundFileSynchronizingMessageSource;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.file.DirectoryScanner;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.metadata.MetadataStore;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

@ConstructionMock({
    S3InboundFileSynchronizingMessageSource.class,
    S3InboundFileSynchronizer.class,
})
class S3InboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private SessionFactory<S3Object> sessionFactory;

    @Mock
    private MetadataStore metadataStore;

    @Mock
    private FileListFilter<File> fileListFilter;

    @Mock
    private Comparator<File> comparator;

    @Mock
    private DirectoryScanner scanner;

    private S3InboundFileSynchronizer synchronizer;

    @Test
    void testAdapter() {
        registerBean("c", DirectChannel.class, new DirectChannel());
        registerBean("sf", SessionFactory.class, sessionFactory);
        registerBean("rfms", MetadataStore.class, metadataStore);
        registerBean("lf", FileListFilter.class, fileListFilter);
        registerBean("cmp", Comparator.class, comparator);
        registerBean("scn", DirectoryScanner.class, scanner);

        var messageSource = loadBean(S3InboundFileSynchronizingMessageSource.class, """
            <int-aws:s3-inbound-channel-adapter
                    id="s3ica"
                    channel="c"
                    local-directory="local"
                    session-factory="sf"
                    delete-remote-files="#{true}"
                    filename-pattern="file*name"
                    local-filename-generator-expression="lfg.exp"
                    local-filter="lf"
                    max-fetch-size="#{10}"
                    metadata-store-prefix="pfx"
                    preserve-timestamp="#{true}"
                    remote-directory-expression="rd.exp"
                    remote-file-metadata-store="rfms"
                    scanner="scn"
                    temporary-file-suffix=".test"
                    remote-file-separator=";"
                    auto-create-local-directory="#{true}"
                    comparator="cmp"/>
            """);

        verify(messageSource).setLocalDirectory(new File("local"));
        verify(messageSource).setScanner(scanner);
        verify(messageSource).setLocalFilter(fileListFilter);
        verify(messageSource).setMaxFetchSize(10);
        verify(messageSource).setAutoCreateLocalDirectory(true);

        verify(synchronizer).setDeleteRemoteFiles(true);
        verify(synchronizer).setRemoteFileMetadataStore(metadataStore);
        verify(synchronizer).setTemporaryFileSuffix(".test");
        verify(synchronizer).setPreserveTimestamp(true);
        verify(synchronizer).setRemoteFileSeparator(";");
        verify(synchronizer).setLocalFilenameGeneratorExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("lfg.exp")));
    }

    void testAdapter(S3InboundFileSynchronizingMessageSource mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(comparator);
    }

    void testAdapter(S3InboundFileSynchronizer mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(sessionFactory);
        synchronizer = mock;
    }
}
