package org.springframework.integration.aws.support.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.inbound.S3InboundFileSynchronizer;
import org.springframework.integration.aws.inbound.S3InboundFileSynchronizingMessageSource;
import org.springframework.integration.aws.support.S3SessionFactory;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.FileListFilter;

import java.io.File;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

@ConstructionMock({
    S3InboundFileSynchronizingMessageSource.class,
    S3InboundFileSynchronizer.class
})
class S3InboundChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private S3SessionFactory sessionFactory;

    @Mock
    private Comparator<File> comparator;

    @Mock
    private FileListFilter<File> localFilter;

    private S3InboundFileSynchronizer synchronizer;

    @Test
    void testAdapter() {
        registerBean("sf", sessionFactory);
        registerBean("cmp", comparator);
        registerBean("lf", localFilter);

        parse("""
            <int-aws:s3-inbound-channel-adapter
                    id="s3ica"
                    channel="c"
                    local-directory="local"
                    session-factory="sf"
                    auto-create-local-directory="#{true}"
                    comparator="cmp"
                    delete-remote-files="#{true}"
                    filename-pattern="file*name"
                    local-filename-generator-expression="file.name"
                    local-filter="lf"
                    preserve-timestamp="#{true}"
                    remote-directory="remote"
                    remote-file-separator=";"
                    temporary-file-suffix=".test"
                    />
            """);

        var messageSource = beanFactory.getBean(S3InboundFileSynchronizingMessageSource.class);

        verify(messageSource).setLocalDirectory(new File("local"));
        verify(messageSource).setLocalFilter(localFilter);
        verify(messageSource).setAutoCreateLocalDirectory(true);

        verify(synchronizer).setDeleteRemoteFiles(true);
        verify(synchronizer).setLocalFilenameGeneratorExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("file.name")));
        verify(synchronizer).setPreserveTimestamp(true);
        verify(synchronizer).setRemoteDirectoryExpression(assertArg(e -> assertThat(e.getValue()).isEqualTo("remote")));
        verify(synchronizer).setRemoteFileSeparator(";");
        verify(synchronizer).setTemporaryFileSuffix(".test");
        verify(synchronizer).setFilter(assertArg(f -> assertThat(f).isInstanceOf(CompositeFileListFilter.class)));
    }

    void testAdapter(S3InboundFileSynchronizingMessageSource mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(synchronizer, comparator);
    }

    void testAdapter(S3InboundFileSynchronizer mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(sessionFactory);
        synchronizer = mock;
    }
}
