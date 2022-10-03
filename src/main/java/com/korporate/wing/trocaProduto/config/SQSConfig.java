package com.korporate.wing.trocaProduto.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.korporate.spring.util.AmbienteEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class SQSConfig
{
    @Value("${aws.accessKey:}")
    private String accessKey;
    @Value("${aws.secretKey:}")
    private String secretKey;
    @Value("${aws.region:}")
    private String region;
    @Value("${aws.sqs.threadPoolSize}")
    private int threadPoolSize;

    @Value("${ambiente}")
    private AmbienteEnum ambiente;

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate()
    {
        return new QueueMessagingTemplate(amazonSQSAsync());
    }

    @Bean
    public AmazonSQSAsync amazonSQSAsync()
    {
        // configuracao padrão do SQS
        AmazonSQSAsyncClientBuilder builder = AmazonSQSAsyncClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)));

        // ambiente dev (LocalStack)
//        if (ambiente == AmbienteEnum.DEV)
//        {
//            builder
//                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", region));
//        }
//        // AWS produção
//        else
//        {
        builder.withRegion(Regions.fromName(region));
//        }

        return builder.build();
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSQS){
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSQS);
        factory.setMaxNumberOfMessages(threadPoolSize);
        return factory;
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(AmazonSQSAsync amazonSQSAsync, QueueMessageHandler queueMessageHandler)
    {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setAmazonSqs(amazonSQSAsync);
        simpleMessageListenerContainer.setMessageHandler(queueMessageHandler);
        simpleMessageListenerContainer.setMaxNumberOfMessages(threadPoolSize);
        simpleMessageListenerContainer.setTaskExecutor(threadPoolTaskExecutor());

        return simpleMessageListenerContainer;
    }

    @Bean
    public QueueMessageHandler queueMessageHandler(AmazonSQSAsync amazonSQSAsync)
    {
        QueueMessageHandlerFactory queueMessageHandlerFactory = new QueueMessageHandlerFactory();
        queueMessageHandlerFactory.setAmazonSqs(amazonSQSAsync);
        return queueMessageHandlerFactory.createQueueMessageHandler();
    }


    public ThreadPoolTaskExecutor threadPoolTaskExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolSize);
        executor.setMaxPoolSize(threadPoolSize);
        executor.initialize();
        executor.setThreadNamePrefix("wing-inclusao-delecao-telefone-sqs-");
        return executor;
    }

}