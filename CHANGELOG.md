# Changelog

## 1.0.0 (2024-10-25)


### Features

* int-aws s3-inbound-channel-adapter xsd schema ([e60df00](https://github.com/boolivar/spring-integration-aws-xml/commit/e60df00e260cc4c570af2325f7adab688d50cfc1))
* **int-aws:** kpl-outbound-channel-adapter xsd schema ([c60bda2](https://github.com/boolivar/spring-integration-aws-xml/commit/c60bda2b12154500754ce291427583cfc5ae3d44))
* kcl-message-driven-channel-adapter xsd schema ([92e94ac](https://github.com/boolivar/spring-integration-aws-xml/commit/92e94ac45baaaa615128181f78a78fafd8581e85))
* kinesis-message-driven-channel-adapter xsd schema ([94c9e84](https://github.com/boolivar/spring-integration-aws-xml/commit/94c9e84710d83084a7cf3baba324738b9c09b07b))
* kinesis-outbound-channel-adapter xsd schema ([7917365](https://github.com/boolivar/spring-integration-aws-xml/commit/7917365df2529ef54cb223021ef3f42307b8ac23))
* s3-inbound-streaming-channel-adapter xsd schema ([560eb38](https://github.com/boolivar/spring-integration-aws-xml/commit/560eb38582fccc59a65057a14bc77ebf0435faf4))
* s3-outbound-channel-adapter xsd schema ([b11c5df](https://github.com/boolivar/spring-integration-aws-xml/commit/b11c5dfec3f8877603ea9b5fa8463e4ca3fa7df8))
* s3-outbound-gateway xsd schema ([8c1e0de](https://github.com/boolivar/spring-integration-aws-xml/commit/8c1e0dee688b24a3730aae71b30ba7e6c616f976))
* S3InboundChannelAdapterParser to parse s3-inbound-channel-adapter ([7b0d715](https://github.com/boolivar/spring-integration-aws-xml/commit/7b0d715a12bad0b9d0203a9c3dc2938f8f7d6715))
* S3InboundStreamingChannelAdapterParser to parse s3-inbound-streaming-channel-adapter ([e1c543c](https://github.com/boolivar/spring-integration-aws-xml/commit/e1c543c9e4b4352d0bb04afad4c49ab01733e74a))
* S3MessageHandlerParser unsupported attributes warning ([27f57be](https://github.com/boolivar/spring-integration-aws-xml/commit/27f57be5b86f5b5e72bfa4f7da1d7fdcda9b9094))
* S3OutboundChannelAdapterParser for s3-outbound-channel-adapter ([71dcedf](https://github.com/boolivar/spring-integration-aws-xml/commit/71dcedf827e77782d51106297b9fee4953cf9a14))
* S3OutboundGatewayParser ([62320d5](https://github.com/boolivar/spring-integration-aws-xml/commit/62320d527de2f752ba0c7f4285d84a5cb27f67b7))
* sns-inbound-channel-adapter xsd schema ([149afd0](https://github.com/boolivar/spring-integration-aws-xml/commit/149afd035844518e6fe95d5ff55d2e2a13ba071c))
* sns-outbound-channel-adapter xsd schema ([2cba20d](https://github.com/boolivar/spring-integration-aws-xml/commit/2cba20d9dae76a70bf597e9ef8ece587fb68ecd5))
* SnsInboundChannelAdapterParser implementation ([35f1457](https://github.com/boolivar/spring-integration-aws-xml/commit/35f1457c97353a795cf162902efee4281d9e6cac))
* SnsOutboundChannelAdapterParser to parse sns-outbound-channel-adapter element ([a598971](https://github.com/boolivar/spring-integration-aws-xml/commit/a5989713df9c4775bb9afe17f265a77f4f8c85b5))
* SpringIntegrationAwsNamespaceHandler element-parser.mapping / META-INF/spring-integration-aws-element-parser.mapping load ([1d81364](https://github.com/boolivar/spring-integration-aws-xml/commit/1d81364c0fb10292d16a225e40a31a61d38b3b48))
* SpringIntegrationAwsNamespaceHandler parsers auto registration ([c08700c](https://github.com/boolivar/spring-integration-aws-xml/commit/c08700c0f94e6fa6614ec138b60c17358f4d6043))
* sqs-message-driven-channel-adapter xsd schema ([b969f9a](https://github.com/boolivar/spring-integration-aws-xml/commit/b969f9a85e98707607c065237e5b11fe8f79282a))
* sqs-outbound-channel-adapter xsd schema ([a17ffbe](https://github.com/boolivar/spring-integration-aws-xml/commit/a17ffbefe39419c59e3458a77733a92e55d23ab5))
* SqsMessageDrivenChannelAdapterParser ([f407049](https://github.com/boolivar/spring-integration-aws-xml/commit/f407049ab148fb983ba9083f1cea4d13640bf54c))
* SqsMessageDrivenChannelAdapterParser expression properties ([c08b31e](https://github.com/boolivar/spring-integration-aws-xml/commit/c08b31e69f4e76264f3e1c0bc998755f502a0586))
* SqsMessageDrivenChannelAdapterParser NO_REDRIVE policy warning ([fd46317](https://github.com/boolivar/spring-integration-aws-xml/commit/fd463178b653ff2bf5fccdbf9a65eda8c65e1d93))
* SqsMessageDrivenChannelAdapterParser sync and success-channel attributes, warn on unsupported attributes ([b1c0d27](https://github.com/boolivar/spring-integration-aws-xml/commit/b1c0d2733fe0c93eee1fe66b52a60533401237ec))
* XmlBeanDefinitionBuilder setPropertyReferenceIfAttributeDefined() ([9aac595](https://github.com/boolivar/spring-integration-aws-xml/commit/9aac5957e8a607f9577bffae3b5a563363a95faa))


### Bug Fixes

* **int-aws:** xsd element annotations ([4cbf182](https://github.com/boolivar/spring-integration-aws-xml/commit/4cbf1829cd963591fb42ed26df91efca822f5c28))
* S3OutboundChannelAdapterParser expression bean ([fb216dc](https://github.com/boolivar/spring-integration-aws-xml/commit/fb216dcc9bb89d0e967604213a283c46a267680e))
* SnsInboundChannelAdapterParser set requestChannelName property ([2b960c0](https://github.com/boolivar/spring-integration-aws-xml/commit/2b960c006bf6cdf36481ccc322f1f6c89ab87fa9))
* SpringIntegrationAwsNamespaceHandler classLoader resource search for package classes ([1e3b14d](https://github.com/boolivar/spring-integration-aws-xml/commit/1e3b14d1e0d27d47703075fde69332b18d68ad21))
* SqsContainerOptionsFactoryBean policy - acknowledgementMode mapping ([22bf8df](https://github.com/boolivar/spring-integration-aws-xml/commit/22bf8df8f7e1aa2a7a9d879583fbb2f20c42fc79))
* SqsOutboundChannelAdapterParser success-channel attribute ([ac0d94d](https://github.com/boolivar/spring-integration-aws-xml/commit/ac0d94dc6d5002eb6aa629ab9c0bf964521650ad))
* XmlBeanDefinitionBuilder setIfExclusiveAttributeDefined if/else logic ([9e16062](https://github.com/boolivar/spring-integration-aws-xml/commit/9e1606296f94a741179076f4e0322c9e5f78acc7))
