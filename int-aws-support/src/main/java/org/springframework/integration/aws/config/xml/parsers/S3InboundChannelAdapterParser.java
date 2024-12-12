package org.springframework.integration.aws.config.xml.parsers;

import org.springframework.integration.aws.inbound.S3InboundFileSynchronizer;
import org.springframework.integration.aws.inbound.S3InboundFileSynchronizingMessageSource;
import org.springframework.integration.aws.support.filters.S3PersistentAcceptOnceFileListFilter;
import org.springframework.integration.aws.support.filters.S3RegexPatternFileListFilter;
import org.springframework.integration.aws.support.filters.S3SimplePatternFileListFilter;
import org.springframework.integration.file.config.AbstractRemoteFileInboundChannelAdapterParser;

public class S3InboundChannelAdapterParser extends AbstractRemoteFileInboundChannelAdapterParser {

    @Override
    protected String getMessageSourceClassname() {
        return S3InboundFileSynchronizingMessageSource.class.getName();
    }

    @Override
    protected Class<S3InboundFileSynchronizer> getInboundFileSynchronizerClass() {
        return S3InboundFileSynchronizer.class;
    }

    @Override
    protected Class<S3SimplePatternFileListFilter> getSimplePatternFileListFilterClass() {
        return S3SimplePatternFileListFilter.class;
    }

    @Override
    protected Class<S3RegexPatternFileListFilter> getRegexPatternFileListFilterClass() {
        return S3RegexPatternFileListFilter.class;
    }

    @Override
    protected Class<S3PersistentAcceptOnceFileListFilter> getPersistentAcceptOnceFileListFilterClass() {
        return S3PersistentAcceptOnceFileListFilter.class;
    }
}
