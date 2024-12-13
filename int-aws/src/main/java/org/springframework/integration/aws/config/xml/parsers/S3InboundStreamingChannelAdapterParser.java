package org.springframework.integration.aws.config.xml.parsers;

import org.springframework.integration.aws.inbound.S3StreamingMessageSource;
import org.springframework.integration.aws.support.S3RemoteFileTemplate;
import org.springframework.integration.aws.support.filters.S3PersistentAcceptOnceFileListFilter;
import org.springframework.integration.aws.support.filters.S3RegexPatternFileListFilter;
import org.springframework.integration.aws.support.filters.S3SimplePatternFileListFilter;
import org.springframework.integration.file.config.AbstractRemoteFileStreamingInboundChannelAdapterParser;

public class S3InboundStreamingChannelAdapterParser extends AbstractRemoteFileStreamingInboundChannelAdapterParser {

    @Override
    protected Class<S3StreamingMessageSource> getMessageSourceClass() {
        return S3StreamingMessageSource.class;
    }

    @Override
    protected Class<S3RemoteFileTemplate> getTemplateClass() {
        return S3RemoteFileTemplate.class;
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
