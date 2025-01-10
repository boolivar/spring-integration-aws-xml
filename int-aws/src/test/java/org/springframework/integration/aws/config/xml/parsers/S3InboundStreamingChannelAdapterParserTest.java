package org.springframework.integration.aws.config.xml.parsers;

import org.bool.junit.mockito.inline.ConstructionMock;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction.Context;
import org.springframework.integration.aws.config.xml.parsers.test.ParserTestBase;
import org.springframework.integration.aws.inbound.S3StreamingMessageSource;
import org.springframework.integration.aws.support.S3RemoteFileTemplate;
import org.springframework.integration.aws.support.S3SessionFactory;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

@ConstructionMock({S3StreamingMessageSource.class, S3RemoteFileTemplate.class})
class S3InboundStreamingChannelAdapterParserTest extends ParserTestBase {

    @Mock
    private S3SessionFactory sessionFactory;

    @Mock
    private Comparator<S3Object> comparator;

    private S3RemoteFileTemplate remoteFileTemplate;

    @Test
    void testAdapter() {
        registerBean("sf", S3SessionFactory.class, sessionFactory);
        registerBean("cmp", Comparator.class, comparator);

        parse("""
            <int-aws:s3-inbound-streaming-channel-adapter
                    id="s3isca"
                    channel="c"
                    session-factory="sf"
                    comparator="cmp"
                    filename-pattern="file*name"
                    max-fetch-size="#{10}"
                    remote-directory="remote"
                    remote-file-separator=";"
                    send-timeout="#{50}"
                    />
            """);

        var messageSource = getBean(S3StreamingMessageSource.class);

        verify(messageSource).setMaxFetchSize(10);
        verify(messageSource).setRemoteDirectoryExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("remote")));
        verify(messageSource).setRemoteFileSeparator(";");
        verify(messageSource).setFilter(assertArg(f -> assertThat(f).isInstanceOf(CompositeFileListFilter.class)));

        verify(remoteFileTemplate).setRemoteDirectoryExpression(assertArg(e -> assertThat(e.getExpressionString()).isEqualTo("remote")));
        verify(remoteFileTemplate).setRemoteFileSeparator(";");
    }

    void testAdapter(S3StreamingMessageSource mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(remoteFileTemplate, comparator);
    }

    void testAdapter(S3RemoteFileTemplate mock, Context context) {
        assertThat(context.arguments()).asInstanceOf(InstanceOfAssertFactories.LIST)
            .contains(sessionFactory);
        remoteFileTemplate = mock;
    }
}
